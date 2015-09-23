package net.headlezz.resdiff

import com.bethecoder.ascii_table.ASCIITable
import org.xml.sax.SAXParseException
import java.io.File
import java.util.*
import kotlin.dom.elements
import kotlin.dom.get
import kotlin.dom.parseXml
import kotlin.reflect.KClass

public fun main(args: Array<String>) {
    // parse arguments
    val clo = CLOptions(args)
    if (clo.hasFlag("h")) {
        clo.printHelp()
        System.exit(0)
    }
    val printTable = clo.hasFlag("y")
    val compareBooleanRes = !clo.hasFlag("t") || clo.getOptionValue("t").contains("bool")
    val compareStringRes = !clo.hasFlag("t") || clo.getOptionValue("t").contains("string")
    val compareColorRes = !clo.hasFlag("t") || clo.getOptionValue("t").contains("color")
    val compareIntegerRes = !clo.hasFlag("t") || clo.getOptionValue("t").contains("integer")
    val compareDimensionRes = !clo.hasFlag("t") || clo.getOptionValue("t").contains("dimen")


    // assert we have two valid file paths
    if (args.size() < 2 ||
            !File(args.get(args.size() - 2)).exists() ||
            !File(args.get(args.size() - 1)).exists()) {
        println("Missing file arguments.")
        clo.printHelp()
        System.exit(1)
    }

    // get all resources
    val resourceList1 = ArrayList<Resource>()
    getFiles(args.get(args.size() - 2)).map { file -> getResourcesFromFile(file) }.forEach { resources -> resourceList1.addAll(resources) }

    val resourceList2 = ArrayList<Resource>()
    getFiles(args.get(args.size() - 1)).map { file -> getResourcesFromFile(file) }.forEach { resources -> resourceList2.addAll(resources) }

    // and compare them!
    val differences = ArrayList<Pair<Resource?, Resource?>>()
    if (compareStringRes)
        differences.addAll(getDifferencesForClass(StringResource::class, resourceList1, resourceList2))
    if (compareBooleanRes)
        differences.addAll(getDifferencesForClass(BooleanResource::class, resourceList1, resourceList2))
    if (compareIntegerRes)
        differences.addAll(getDifferencesForClass(IntegerResource::class, resourceList1, resourceList2))
    if (compareDimensionRes)
        differences.addAll(getDifferencesForClass(DimensionResource::class, resourceList1, resourceList2))
    if (compareColorRes)
        differences.addAll(getDifferencesForClass(ColorResource::class, resourceList1, resourceList2))

    val addCount = differences.count { pair -> pair.first == null && pair.second != null }
    val delCount = differences.count { pair -> pair.first != null && pair.second == null }
    println("Found ${differences.size()} differences ($addCount added, $delCount removed).")

    if (printTable)
        printDiffInTable(differences)
    else
        printDiffNormal(differences)
}

fun printDiffNormal(differences: List<Pair<Resource?, Resource?>>) {
    differences.forEach { pair ->
        val resA = pair.first
        val resB = pair.second
        val type = (resA?.type ?: resB!!.type).toString().toLowerCase()
        val name = resA?.name ?: resB!!.name
        println("<$type name=\"$name\">")
        if (resA != null)
            println("<\t${resA.value}")
        if (resA != null && resB != null)
            println("---")
        if (resB != null)
            println(">\t${resB.value}")
    }
}

fun printDiffInTable(differences: List<Pair<Resource?, Resource?>>) {
    val header = arrayOf("Type", "Name", "Left", "Right")
    val data = differences.map { pair ->
        val type = (pair.first?.type ?: pair.second!!.type).toString()
        val name = pair.first?.name ?: pair.second!!.name
        arrayOf(type, name, pair.first?.value ?: "", pair.second?.value ?: "")
    }.toTypedArray()
    ASCIITable.getInstance().printTable(header, data)
}

fun getDifferencesForClass(clazz: KClass<out Resource>, resourceList1: ArrayList<Resource>, resourceList2: ArrayList<Resource>): List<Pair<Resource?, Resource?>> {
    val className = clazz.simpleName
    return getDifferences(
            resourceList1.filter { r -> r.javaClass.simpleName == className },
            resourceList2.filter { r -> r.javaClass.simpleName == className })
}

fun getDifferences(from: List<Resource>, to: List<Resource>): ArrayList<Pair<Resource?, Resource?>> {
    val diffs = ArrayList<Pair<Resource?, Resource?>>()

    val pairs = genResourcePairs(from, to)

    pairs.forEach { pair ->
        if (!Resource.match(pair.first, pair.second))
            diffs.add(pair)
    }

    return diffs
}

/**
 * creates Pairs of resource with matching identity
 */
fun genResourcePairs(from: List<Resource>, to: List<Resource>): List<Pair<Resource?, Resource?>> {
    val pairs = ArrayList<Pair<Resource?, Resource?>>()

    val tempFrom = ArrayList<Resource>(from.size())
    tempFrom.addAll(from)
    val tempTo = ArrayList<Resource>(to.size())
    tempTo.addAll(to)

    loop@for (f in tempFrom) {
        for (t in tempTo) {
            if (f.matchIdent(t)) {
                pairs.add(Pair(f, t))
                continue@loop
            }
        }
        pairs.add(Pair(f, null))
    }
    pairs.forEach { pair ->
        tempFrom.remove(pair.first)
        tempTo.remove(pair.second)
    }
    tempTo.forEach { t -> pairs.add(Pair(null, t)) }

    return pairs
}

/**
 * If location points to a directory, returns all files in this directory (not recursive), else,
 * it just returns the file
 */
fun getFiles(location: String): Array<File> {
    val file = File(location)
    if (file.isDirectory) {
        return file.listFiles { file, filter -> true }.filterNot { file -> file.isDirectory }.toTypedArray()
    } else
        return arrayOf(file)
}

/**
 * returns a list of all resources found in the given file
 */
fun getResourcesFromFile(file: File): ArrayList<Resource> {
    try {
        val doc = parseXml(file.absoluteFile)
        val resources = doc.get("resources").first()
        return resources.elements.map { elem -> Resource.fromElement(elem) }.filterNotNull().toArrayList()
    } catch(e: SAXParseException) {
        println("${file.name} is not a valid xml file")
        return arrayListOf()
    } catch(e: NoSuchElementException) {
        println("${file.name} is not a valid xml file")
        return arrayListOf()
    }
}
