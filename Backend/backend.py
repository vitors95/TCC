from flask import Flask, jsonify
from flask_cors import CORS
from flask import abort
from flask import make_response
from flask import request
from pyfcm import FCMNotification
import numpy as np
import pandas as pd
from sklearn.cluster import KMeans

import time
import sys
import mysql.connector

push_service = FCMNotification(api_key="AAAAd_quocY:APA91bH4wZXhrSJQ3KCRqvmmVc39agsuNo0qq4lT2HwDb9Eq4Guo-5yspMnwcWOrtkjiY9tankvx5gTcYjktldd82QSYoIp5HezvYeePLOhOFPi9oWHE4SNl564oO2xlPdDxaHs6mqEP")

app = Flask(__name__)
CORS(app)

###################################    INTEGRAÇÃO COM O K-MEANS        ###################################

predictionCount = 0

def prediction(col):
	dataset = pd.read_csv('dataset_normalized_acceleration.csv')
	
	resultado = []
	dictionary = {}

	placeID = 1
	(cr, cnx) = openConnection()
	query = "SELECT * FROM (SELECT * FROM Collect WHERE Place_idPlace = (%s) ORDER BY idCollect DESC LIMIT 5) sub ORDER BY idCollect ASC"
	dados = (placeID, )
		
	cr.execute(query, dados)
	for collectRow in cr.fetchall():
		dictionary.update({'accx': collectRow[1]})
		dictionary.update({'accy': collectRow[2]})
		dictionary.update({'accz': collectRow[3]})
		dictionary.update({'rmsx': collectRow[4]})
		dictionary.update({'rmsy': collectRow[5]})
		dictionary.update({'rmsz': collectRow[6]})
		resultado.append(dictionary)
		dictionary = {}

	closeConnection(cr, cnx)  
		
	dataset_last_5 = pd.DataFrame({
							"accx": [resultado[0]['accx']/8192, resultado[1]['accx']/8192, resultado[2]['accx']/8192, resultado[3]['accx']/8192, resultado[4]['accx']/8192], 
							"accy": [resultado[0]['accy']/8192, resultado[1]['accy']/8192, resultado[2]['accy']/8192, resultado[3]['accy']/8192, resultado[4]['accy']/8192],
							"accz": [resultado[0]['accz']/8192, resultado[1]['accz']/8192, resultado[2]['accz']/8192, resultado[3]['accz']/8192, resultado[4]['accz']/8192],
							"rmsx": [resultado[0]['rmsx']/8192, resultado[1]['rmsx']/8192, resultado[2]['rmsx']/8192, resultado[3]['rmsx']/8192, resultado[4]['rmsx']/8192],
							"rmsy": [resultado[0]['rmsy']/8192, resultado[1]['rmsy']/8192, resultado[2]['rmsy']/8192, resultado[3]['rmsy']/8192, resultado[4]['rmsy']/8192],
							"rmsz": [resultado[0]['rmsz']/8192, resultado[1]['rmsz']/8192, resultado[2]['rmsz']/8192, resultado[3]['rmsz']/8192, resultado[4]['rmsz']/8192]
							}) 
    
	dataset = dataset.append(dataset_last_5, ignore_index=True)	

	print(dataset, file=sys.stderr)
	
	algorithm = KMeans(n_clusters = 2, init='k-means++', n_init = 10 ,max_iter=300, tol=0.0001, random_state= 1, algorithm='elkan')
    
    ### Eixo X ###
	X = dataset[['rmsx' , 'accx']].iloc[: , :].values    
	algorithm.fit(X)    
	x_pred_status = algorithm.labels_
        
    ### Eixo Y ###
    
	Y = dataset[['rmsy' , 'accy']].iloc[: , :].values   
	algorithm.fit(Y)
	y_pred_status = algorithm.labels_
        
    ### Eixo Z ###
	Z = dataset[['rmsz' , 'accz']].iloc[: , :].values
	algorithm.fit(Z)
	z_pred_status = algorithm.labels_  
    
	anormal = {}
	anormal['X'] = sum(x_pred_status[-5:])
	anormal['Y'] = sum(y_pred_status[-5:])
	anormal['Z'] = sum(z_pred_status[-5:])   
    
	return anormal
	
def predictionAlert(col):
	global predictionCount 
	predictionCount = predictionCount + 1
	
	place = ""
	equipment = ""

	(cr, cnx) = openConnection()
	query = "SELECT P.description, E.description FROM Place P INNER JOIN Endpoint D ON D.Place_idPlace = P.idPlace INNER JOIN Equipment E ON E.idEquipment = P.Equipment_idEquipment WHERE D.mac = (%s)"
	dados = (col.endpoint['mac'],)

	cr.execute(query, dados)

	for description in cr.fetchall():
		place = description[0]
		equipment = description[1]

	closeConnection(cr, cnx)
	
	if predictionCount == 5:
		predictionCount = 0
		anormal_prediction = prediction(col)
		if (anormal_prediction['X'] > 2) or (anormal_prediction['Y'] > 2) or (anormal_prediction['Z'] > 2):
			message = 'Detecção de comportamento anômalo no ' + equipment + ' (' +  + ')'
			result = push_service.notify_topic_subscribers(topic_name="all", message_title="ATENÇÃO", message_body=message, content_available=True)
	
	temp = col.collect['temp']
	if temp > 1860:
		temp = (temp/340)+36.53
		temp = round(temp, 2)
		message = 'A temperatura do ' + equipment + ' (' + place + ')' + ' é de ' + str(temp) + ' °C'
		result = push_service.notify_topic_subscribers(topic_name="all", message_title="ATENÇÃO", message_body=message, content_available=True)
	
def openConnection():
    cnx = mysql.connector.connect(
		user='tcc', 
		password='tcc20192', 
		host='localhost', 
		database='dbtcc', 
		auth_plugin='mysql_native_password'
		)
    cr = cnx.cursor(buffered=True)
    return (cr, cnx)


def closeConnection(cr, cnx):
    cr.close()
    cnx.close()

class collectClass:
	def __init__(self, _collect, _gateway, _endpoint):
		self.collect = _collect
		self.gateway = _gateway
		self.endpoint = _endpoint

###################################    COLETA        ###################################

@app.route('/collect', methods=['POST', 'GET'])
def receiveCollect():
	if request.method == "GET":
		
		placeID = request.args.get("place_id")
				
		resultado = []
		dictionary = {}

		(cr, cnx) = openConnection()
		query = "SELECT * FROM Collect WHERE Place_idPlace = (%s)"
		dados = (placeID, )
		
		cr.execute(query, dados)

		for collectRow in cr.fetchall():
			dictionary.update({'collect_id': collectRow[0]})
			dictionary.update({'accx': collectRow[1]})
			dictionary.update({'accy': collectRow[2]})
			dictionary.update({'accz': collectRow[3]})
			dictionary.update({'rmsx': collectRow[4]})
			dictionary.update({'rmsy': collectRow[5]})
			dictionary.update({'rmsz': collectRow[6]})
			dictionary.update({'temp': collectRow[7]})
			dictionary.update({'data': str(collectRow[8])})
			resultado.append(dictionary)
			dictionary = {}

		closeConnection(cr, cnx)
		
		return jsonify(resultado)
		
	else:
		print(request.json, file=sys.stderr)
		if not request.json:
			abort(400)
			
		col = collectClass(
				request.json['collect'], 
				request.json['gateway'], 
				request.json['endpoint']
				)
			
		predictionAlert(col)		
	
	##########    Verifica se existe o endpoint e obtém o placeID     ##########
	
		queryEndpoint = ("SELECT Place_idPlace FROM Endpoint WHERE mac = ('%s')" % (col.endpoint['mac'])) 
		(cr,cnx) = openConnection()
		cr.execute(queryEndpoint)	
		if cr.rowcount == 0:
			closeConnection(cr, cnx)
			return jsonify({'error': {'code': 1, 'description': 'Endpoint inexistente.'}})
		else:
			result = cr.fetchall()
			idPlace = int(result[0][0])
			closeConnection(cr, cnx)
		closeConnection(cr, cnx)
	
		
	##########    Verifica se existe o gateway, se não, o cria      ##########
			
		queryGateway1 = ("SELECT idGateway FROM Gateway WHERE mac = ('%s')" % (col.gateway['mac'])) 
		(cr,cnx) = openConnection()
		cr.execute(queryGateway1)	
		if cr.rowcount == 0:
			queryGateway2 = ("INSERT INTO Gateway (mac) VALUES ('%s')" % (col.gateway['mac']))
			try: 
				cr.execute(queryGateway2)
				cnx.commit()
				cr.execute(queryGateway1)
				result = cr.fetchall()
				idGateway = int(result[0][0])
				closeConnection(cr, cnx)
			except mysql.connector.Error as e: 
				print(e, file=sys.stderr)
				closeConnection(cr, cnx)
				return jsonify({'error': {'code': e.errno, 'description': e.msg}})
		else: 
			result = cr.fetchall()
			idGateway = int(result[0][0])
			closeConnection(cr, cnx)
	
	########## Grava no banco a coleta recebida do gateway         ##########
	
		queryCollect = ("INSERT INTO Collect (accx, accy, accz, rmsx, rmsy, rmsz, temp, data, Gateway_idGateway, Place_idPlace) VALUES (%d, %d, %d, %d, %d, %d, %d, TIMESTAMP(NOW()), %d, %d)" % 
			  (col.collect['accx'], col.collect['accy'], col.collect['accz'], col.collect['rmsx'], col.collect['rmsy'], col.collect['rmsz'], col.collect['temp'], idGateway, idPlace))
		(cr,cnx) = openConnection()
		try: 
			cr.execute(queryCollect)
			cnx.commit()
			closeConnection(cr, cnx)
		except mysql.connector.Error as e:
			print(e, file=sys.stderr)
			closeConnection(cr, cnx)
			return jsonify({'error': {'code': e.errno, 'description': e.msg}})
			
		return jsonify({'OK': 201})

########## EQUIPMENTS  ###############
	
@app.route('/equipments', methods=['POST', 'GET'])
def equipments():
	if request.method == 'POST':
		if not request.json:
			abort(400)
			
		description = request.json['description']

		(cr, cnx) = openConnection()

		query = "INSERT INTO Equipment (description) VALUES (%s)"
		dados = (description, )

		try: 
			cr.execute(query, dados)
			cnx.commit()
			closeConnection(cr, cnx)
		except mysql.connector.Error as e:
			print(e, file=sys.stderr)
			closeConnection(cr, cnx)
			return jsonify({'error': {'code': e.errno, 'description': e.msg}})
		
		return jsonify({'OK': 201})

	else:
		resultado = []
		dictionary = {}

		(cr, cnx) = openConnection()
		query = "SELECT * FROM Equipment"

		cr.execute(query)

		for equipment in cr.fetchall():
			dictionary.update({'equipment_id': equipment[0]})
			dictionary.update({'description': equipment[1]})
			resultado.append(dictionary)
			dictionary = {}

		closeConnection(cr, cnx)
		
		return jsonify(resultado)

########## PLACES  ###############
	
@app.route('/places', methods=['POST', 'GET'])
def places():
	if request.method == 'POST':
		if not request.json:
			abort(400)
			
		description = request.json['description']
		equipmentID = request.json['equipment_id']

		(cr, cnx) = openConnection()

		query = "INSERT INTO Place (description, Equipment_idEquipment) VALUES (%s, %s)"
		dados = (description, equipmentID)

		try: 
			cr.execute(query, dados)
			cnx.commit()
			closeConnection(cr, cnx)
		except mysql.connector.Error as e:
			print(e, file=sys.stderr)
			closeConnection(cr, cnx)
			return jsonify({'error': {'code': e.errno, 'description': e.msg}})
		
		return jsonify({'OK': 201})

	else:
		resultado = []
		dictionary = {}

		(cr, cnx) = openConnection()
		query = "SELECT P.idPlace, P.description, E.idEquipment, E.description FROM Place P INNER JOIN Equipment E ON E.idEquipment = P.Equipment_idEquipment"

		cr.execute(query)

		for place in cr.fetchall():
			dictionary.update({'place_id': place[0]})
			dictionary.update({'place_description': place[1]})
			dictionary.update({'equipment_id': place[2]})
			dictionary.update({'equipment_description': place[3]})
			resultado.append(dictionary)
			dictionary = {}

		closeConnection(cr, cnx)
		
		return jsonify(resultado)
	
########## ENDPOINT  ###############
	
@app.route('/endpoints', methods=['POST', 'GET'])
def endpoints():
	if request.method == 'POST':
		if not request.json:
			abort(400)
			
		mac = request.json['mac']
		placeID = request.json['place_id']

		(cr, cnx) = openConnection()

		query = "INSERT INTO Endpoint (mac, Place_idPlace) VALUES (%s, %s)"
		dados = (mac, placeID)

		try: 
			cr.execute(query, dados)
			cnx.commit()
			closeConnection(cr, cnx)
		except mysql.connector.Error as e:
			print(e, file=sys.stderr)
			closeConnection(cr, cnx)
			return jsonify({'error': {'code': e.errno, 'description': e.msg}})
		
		return jsonify({'OK': 201})

	else:
		resultado = []
		dictionary = {}

		(cr, cnx) = openConnection()
		query = "SELECT * FROM Endpoint"

		cr.execute(query)

		for endpoint in cr.fetchall():
			dictionary.update({'endpoint_id': endpoint[0]})
			dictionary.update({'mac': endpoint[1]})
			dictionary.update({'place_id': endpoint[2]})
			resultado.append(dictionary)
			dictionary = {}

		closeConnection(cr, cnx)
		
		return jsonify(resultado)
	
###################################        QUANDO COM ERROS        ###################################
		
@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

if __name__ == "__main__":
	app.run(host="0.0.0.0", port=5000, debug=True)
