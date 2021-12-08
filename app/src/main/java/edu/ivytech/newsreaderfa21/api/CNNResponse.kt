package edu.ivytech.newsreaderfa21.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
class CNNResponse {
    @field:Path("channel")
    @field:ElementList(name="item", inline=true)
    lateinit var articles : List<CNNArticle>
}
@Root(name = "item", strict = false)
data class CNNArticle(
    @field:Element(name="title")
    @param:Element(name="title")
    var title : String = "",
    @field:Element(name="description")
    @param:Element(name="description")
    var description : String = "",
    @field:Element(name="pubDate")
    @param:Element(name="pubDate")
    var date : String = "",
    @field:Element(name="link")
    @param:Element(name="link")
    var link : String = "")
