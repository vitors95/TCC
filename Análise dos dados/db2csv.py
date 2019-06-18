import mysql.connector
import pandas


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

#query = ("SELECT dev, AVG(valor), 10*LOG10(distancia), time FROM coleta WHERE dev != '%s' AND distancia != 0;" % dispositivo)
#query = ("SELECT LOG10(distancia), AVG(valor) FROM coleta WHERE distancia != 0 AND dev = '%s' GROUP BY distancia;" % dispositivo)
query = 'SELECT * FROM Collect'

(cr,cnx) = openConnection()

results = pandas.read_sql_query(query, cnx)
results.to_csv("output.csv", index=False)

closeConnection(cr, cnx)