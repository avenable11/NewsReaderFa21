package edu.ivytech.newsreaderfa21.api

import com.google.gson.annotations.SerializedName

data class NYTResponse(var results : List<NYTArticle>)

data class NYTArticle(val title:String,
                      @SerializedName("published_date")val date : String,
                      @SerializedName("abstract")val description:String,
                      val url : String)

//data class Link(val url : String?)