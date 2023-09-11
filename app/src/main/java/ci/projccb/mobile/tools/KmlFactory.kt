package ci.projccb.mobile.tools

import com.google.android.gms.maps.model.LatLng
import org.joda.time.DateTime
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * A class to represent, write, and read KML files.
 *
 * @author Ethan Harstad
 */
object KmlFactory {


    private var doc: Document? = null
    private var root: Element? = null


    /**
     * Add a placemark to this KML object.
     * @param mark
     */
    fun addMark(mark: LatLng) {
        val placemark = doc!!.createElement("Placemark")
        root!!.appendChild(placemark)
        val name = doc!!.createElement("name")
        name.appendChild(doc!!.createTextNode("Mark Name"))
        placemark.appendChild(name)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
        val desc = doc!!.createElement("description")
        desc.appendChild(
            doc!!.createTextNode(
                """${mark.latitude}, ${mark.longitude} Time: ${sdf.format(DateTime.now())} """.trimIndent()
            )
        )

        placemark.appendChild(desc)
        val point = doc!!.createElement("Point")
        placemark.appendChild(point)

        /*if(mark.getAltitude() > 0) {
			Element altitudeMode = doc.createElement("altitudeMode");
			altitudeMode.appendChild(doc.createTextNode("absolute"));
			point.appendChild(altitudeMode);
		}*/

        val coords = doc!!.createElement("coordinates")
        coords.appendChild(doc!!.createTextNode(mark.longitude.toString() + ", " + mark.latitude))
        point.appendChild(coords)
    }

    /**
     * Add a path to this KML object.
     * @param path
     * @param pathName
     */
    fun addPath(path: List<LatLng?>, pathName: String?) {
        val placemark = doc!!.createElement("Placemark")
        root!!.appendChild(placemark)
        if (pathName != null) {
            val name = doc!!.createElement("name")
            name.appendChild(doc!!.createTextNode(pathName))
            placemark.appendChild(name)
        }
        val lineString = doc!!.createElement("LineString")
        placemark.appendChild(lineString)
        val extrude = doc!!.createElement("extrude")
        extrude.appendChild(doc!!.createTextNode("1"))
        lineString.appendChild(extrude)
        val tesselate = doc!!.createElement("tesselate")
        tesselate.appendChild(doc!!.createTextNode("1"))
        lineString.appendChild(tesselate)
        val altitudeMode = doc!!.createElement("altitudeMode")
        altitudeMode.appendChild(doc!!.createTextNode("absolute"))
        lineString.appendChild(altitudeMode)
        val coords = doc!!.createElement("coordinates")
        var points = ""
        /*ListIterator and lt
        MapPoint > itr = path.listIterator()
        while (itr.hasNext()) {
            val p: MapPoint = itr.next()
            points += p.getLongitude()
                .toString() + "," + p.getLatitude() + "," + p.getAltitude() + "\n"
        }*/
        coords.appendChild(doc!!.createTextNode(points))
        lineString.appendChild(coords)
    }


    /**
     * Write this KML object to a file.
     * @param file
     * @return
     */
    fun writeFile(file: File?): Boolean {
        try {
            val factory = TransformerFactory.newInstance()
            val transformer = factory.newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val src = DOMSource(doc)
            val out = StreamResult(file)
            transformer.transform(src, out)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * Read the KML file into this object.
     * @param file
     */
    fun readFile(file: File?) {
        // TODO read KML file
    }

    /**
     * Create a KML object.
     */
    init {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            doc = builder.newDocument()
            val kml = doc?.createElementNS("http://www.opengis.net/kml/2.2", "kml")
            doc?.appendChild(kml)
            root = doc?.createElement("Document")
            kml?.appendChild(root)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
