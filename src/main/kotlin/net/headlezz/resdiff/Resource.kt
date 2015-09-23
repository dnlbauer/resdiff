package net.headlezz.resdiff

import org.w3c.dom.Element

abstract class Resource(
        public val name: String,
        public val value: String,
        public val type: Resource.Type) {

    enum class Type {
        String, Dimension, Boolean, Integer, Color
    }

    companion object {
        public fun fromElement(elem: Element) : Resource? {
            val type= elem.tagName
            val name = elem.getAttribute("name")
            val value = elem.firstChild.nodeValue

            return when(type) {
                "string" -> StringResource(name, value)
                "dimen" -> DimensionResource(name, value)
                "bool" -> BooleanResource(name, value)
                "integer" -> IntegerResource(name, value)
                "color" -> ColorResource(name, value)
                else -> null
            }
        }

        public fun match(resource1: Resource?, resource2: Resource?) : Boolean {
            if(resource1 == null)
                return false

            if(resource2 == null)
                return false

            return resource1.match(resource2)
        }

    }

    /**
     * Check if the given resource matches the this one
     */
    public fun match(resource2: Resource) : Boolean {
        return matchIdent(resource2) && value == resource2.value
    }

    /**
     * Check if identity of two resources match (resource name matching)
     */
    public fun matchIdent(resource2: Resource) : Boolean {
        return this.type == resource2.type && this.name == resource2.name
    }
}

class StringResource(name: String, value: String) : Resource(name, value, Resource.Type.String)
class BooleanResource(name: String, value: String) : Resource(name, value, Resource.Type.Boolean)
class ColorResource(name: String, value: String) : Resource(name, value, Resource.Type.Color)
class IntegerResource(name: String, value: String) : Resource(name, value, Resource.Type.Integer)
class DimensionResource(name: String, value: String) : Resource(name, value, Resource.Type.Dimension)
