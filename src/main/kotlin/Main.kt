package org.example

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilder

fun main() {

    val rutaCSV = "src/main/resources/empleados.csv"
    val rutaXML = "src/main/resources/empleados.xml"

    // 1. Lectura de empleados desde archivo de texto:
    val empleados = leerEmpleadosDesdeCSV(rutaCSV)

    // 2. Generación de un archivo XML:
    genracionXml(empleados, rutaXML)

    // 3. Modificación de un nodo en el archivo XML:
    modificacionXml(rutaXML, "5", "4500")

    // 4. Lectura del archivo XML modificado y salida en consola:
    leerXML(rutaXML)
}

// Función para leer los empleados desde el archivo CSV

fun leerEmpleadosDesdeCSV(rutaArchivo: String): List<Map<String, String>> {

    val empleados = mutableListOf<Map<String, String>>()

    val path = Path.of(rutaArchivo)
    Files.newBufferedReader(path).use { reader: BufferedReader ->

        // Leer la primera línea
        val cabecera = reader.readLine().split(",")

        /*El metodo zip es una función que combina dos listas en pares, emparejando los elementos correspondientes de
        ambas listas por sus posiciones.*/

        // Leer los datos del empleado
        reader.forEachLine { linea ->
            val datos = linea.split(",")
            val empleado = cabecera.zip(datos).toMap()
            empleados.add(empleado)
        }
    }

    return empleados
}

// Función para generar el archivo XML a partir de la lista de empleados
fun genracionXml(empleados: List<Map<String, String>>, rutaArchivoXML: String) {

    val docFactory = DocumentBuilderFactory.newInstance()
    val docBuilder = docFactory.newDocumentBuilder()

    // Creamos un nuevo documento XML
    val doc: Document = docBuilder.newDocument()
    val rootElement = doc.createElement("empleados")
    doc.appendChild(rootElement)

    // Añadimos los empleados al documento XML

    empleados.forEach { empleado ->

        val empleadoElement = doc.createElement("empleado")
        empleadoElement.setAttribute("id", empleado["ID"])

        //Utilizamos el appendChild para agregar como nodos hijos los datos a empleado.

        val apellidoElement = doc.createElement("apellido")
        apellidoElement.appendChild(doc.createTextNode(empleado["Apellido"]))
        empleadoElement.appendChild(apellidoElement)

        val departamentoElement = doc.createElement("departamento")
        departamentoElement.appendChild(doc.createTextNode(empleado["Departamento"]))
        empleadoElement.appendChild(departamentoElement)

        val salarioElement = doc.createElement("salario")
        salarioElement.appendChild(doc.createTextNode(empleado["Salario"]))
        empleadoElement.appendChild(salarioElement)

        rootElement.appendChild(empleadoElement)
    }

    // Transformer es una herramienta para convertir el árbol DOM en un fichero XML.

    // Guardar el documento XML en archivo

    val transformerFactory = TransformerFactory.newInstance()
    val transformer = transformerFactory.newTransformer()

    val source = DOMSource(doc)
    val result = StreamResult(File(rutaArchivoXML))
    transformer.transform(source, result)
}

// Función para modificar el salario de un empleado en el archivo XML

fun modificacionXml(rutaArchivoXML: String, idEmpleado: String, nuevoSalario: String) {

    val docBuilderFactory = DocumentBuilderFactory.newInstance()
    val docBuilder: DocumentBuilder = docBuilderFactory.newDocumentBuilder()
    val doc: Document = docBuilder.parse(File(rutaArchivoXML))

    /* Encontramos el nodo del empleado por ID y modificamos el salario. Utilizamos textContent para modificar el salario,
    textContent accede a un texto y modifica el contenido */

    val empleados = doc.getElementsByTagName("empleado")

    for (i in 0 until empleados.length) {
        val empleado = empleados.item(i) as Element
        if (empleado.getAttribute("id") == idEmpleado) {
            val salario = empleado.getElementsByTagName("salario").item(0)
            salario.textContent = nuevoSalario
            break
        }
    }

    // Guardamos los cambios en el archivo XML
    val transformerFactory = TransformerFactory.newInstance()
    val transformer = transformerFactory.newTransformer()

    val source = DOMSource(doc)
    val result = StreamResult(File(rutaArchivoXML))
    transformer.transform(source, result)
}

    // Función para leer el archivo XML y mostrar los datos en la consola
    fun leerXML (rutaArchivoXML: String) {
        val docBuilderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder: DocumentBuilder = docBuilderFactory.newDocumentBuilder()
        val doc: Document = docBuilder.parse(File(rutaArchivoXML))

        // Obtener todos los empleados y mostrar sus datos
        val empleados = doc.getElementsByTagName("empleado")
        for (i in 0 until empleados.length) {
            val empleado = empleados.item(i) as Element
            val id = empleado.getAttribute("id")
            val apellido = empleado.getElementsByTagName("apellido").item(0).textContent
            val departamento = empleado.getElementsByTagName("departamento").item(0).textContent
            val salario = empleado.getElementsByTagName("salario").item(0).textContent

            println("ID: $id, Apellido: $apellido, Departamento: $departamento, Salario: $salario")
        }
    }
