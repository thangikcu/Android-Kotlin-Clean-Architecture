package com.development.clean.feature.searchphoto

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class SearchPhotoResponse(
    @Json(name = "results")
    val results: List<Photo>,
    @Json(name = "total")
    val total: Int, // 10000
    @Json(name = "total_pages")
    val totalPages: Int // 10000
) {
    @JsonClass(generateAdapter = true)
    @Entity
    data class Photo(
        @PrimaryKey
        @Json(name = "id")
        val id: String, // -YHSwy6uqvk
        @Json(name = "created_at")
        val createdAt: String?, // 2017-09-06T01:16:19-04:00
        @Json(name = "width")
        val width: Int?, // 4350
        @Json(name = "height")
        val height: Int?, // 2900
        @Json(name = "description")
        val description: String?, // null
        @Json(name = "urls")
        val urls: Urls,
        @Json(name = "likes")
        val likes: Int?, // 946
        @Json(name = "liked_by_user")
        val likedByUser: Boolean?, // false
        @Json(name = "topic_submissions")
        val topicSubmissions: TopicSubmissions?,
        @Json(name = "user")
        val user: User?,
        @Json(name = "tags")
        val tags: List<Tag>?
    ) {
        @JsonClass(generateAdapter = true)
        data class Urls(
            @Json(name = "raw")
            val raw: String?, // https://images.unsplash.com/photo-1504674900247-0877df9cc836?ixid=MnwzNDQ1MDl8MHwxfHNlYXJjaHwxfHxmb29kfGVufDB8fHx8MTY1NzUyODUzOQ&ixlib=rb-1.2.1
            @Json(name = "full")
            val full: String?, // https://images.unsplash.com/photo-1504674900247-0877df9cc836?crop=entropy&cs=tinysrgb&fm=jpg&ixid=MnwzNDQ1MDl8MHwxfHNlYXJjaHwxfHxmb29kfGVufDB8fHx8MTY1NzUyODUzOQ&ixlib=rb-1.2.1&q=80
            @Json(name = "regular")
            val regular: String?, // https://images.unsplash.com/photo-1504674900247-0877df9cc836?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzNDQ1MDl8MHwxfHNlYXJjaHwxfHxmb29kfGVufDB8fHx8MTY1NzUyODUzOQ&ixlib=rb-1.2.1&q=80&w=1080
            @Json(name = "small")
            val small: String?, // https://images.unsplash.com/photo-1504674900247-0877df9cc836?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzNDQ1MDl8MHwxfHNlYXJjaHwxfHxmb29kfGVufDB8fHx8MTY1NzUyODUzOQ&ixlib=rb-1.2.1&q=80&w=400
            @Json(name = "thumb")
            val thumb: String?, // https://images.unsplash.com/photo-1504674900247-0877df9cc836?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzNDQ1MDl8MHwxfHNlYXJjaHwxfHxmb29kfGVufDB8fHx8MTY1NzUyODUzOQ&ixlib=rb-1.2.1&q=80&w=200
            @Json(name = "small_s3")
            val smallS3: String? // https://s3.us-west-2.amazonaws.com/images.unsplash.com/small/photo-1504674900247-0877df9cc836
        )

        @JsonClass(generateAdapter = true)
        data class TopicSubmissions(
            @Json(name = "food-drink")
            val foodDrink: TopicStatus?,
            @Json(name = "wallpapers")
            val wallpapers: TopicStatus?,
            @Json(name = "spirituality")
            val spirituality: TopicStatus?,
            @Json(name = "nature")
            val nature: TopicStatus?,
            @Json(name = "textures-patterns")
            val texturesPatterns: TopicStatus?,
            @Json(name = "sustainability")
            val sustainability: TopicStatus?,
        ) {
            @JsonClass(generateAdapter = true)
            data class TopicStatus(
                @Json(name = "status")
                val status: String?, // approved
                @Json(name = "approved_on")
                val approvedOn: String? // 2020-04-06T10:20:20-04:00
            )
        }

        @JsonClass(generateAdapter = true)
        data class User(
            @Json(name = "id")
            val id: String?, // LJ-IkrVj9D8
            @Json(name = "updated_at")
            val updatedAt: String?, // 2022-07-11T03:23:53-04:00
            @Json(name = "username")
            val username: String?, // lvnatikk
            @Json(name = "name")
            val name: String?, // Lily Banse
            @Json(name = "first_name")
            val firstName: String?, // Lily
            @Json(name = "last_name")
            val lastName: String?, // Banse
            @Json(name = "twitter_username")
            val twitterUsername: String?, // LVnatikk
            @Json(name = "portfolio_url")
            val portfolioUrl: String?, // http://instagram.com/lilybanse
            @Json(name = "bio")
            val bio: String?, // Me Shoot Anything📸Previously well known as Seventeen10 📧banse.lily@gmail.com www.facebook.com/seventeen10 📧seventeen10photography@outlook.com Owner @lvnatikk | @anything35mm
            @Json(name = "location")
            val location: String?, // Singapore
            @Json(name = "profile_image")
            val profileImage: ProfileImage?,
            @Json(name = "instagram_username")
            val instagramUsername: String?, // Lilybanse
            @Json(name = "total_collections")
            val totalCollections: Int?, // 12
            @Json(name = "total_likes")
            val totalLikes: Int?, // 42
            @Json(name = "total_photos")
            val totalPhotos: Int?, // 112
            @Json(name = "accepted_tos")
            val acceptedTos: Boolean?, // true
            @Json(name = "for_hire")
            val forHire: Boolean?, // false
            @Json(name = "social")
            val social: Social?
        ) {
            @JsonClass(generateAdapter = true)
            data class ProfileImage(
                @Json(name = "small")
                val small: String?, // https://images.unsplash.com/profile-1610362018804-6faf7d2338eaimage?ixlib=rb-1.2.1&crop=faces&fit=crop&w=32&h=32
                @Json(name = "medium")
                val medium: String?, // https://images.unsplash.com/profile-1610362018804-6faf7d2338eaimage?ixlib=rb-1.2.1&crop=faces&fit=crop&w=64&h=64
                @Json(name = "large")
                val large: String? // https://images.unsplash.com/profile-1610362018804-6faf7d2338eaimage?ixlib=rb-1.2.1&crop=faces&fit=crop&w=128&h=128
            )

            @JsonClass(generateAdapter = true)
            data class Social(
                @Json(name = "instagram_username")
                val instagramUsername: String?, // Lilybanse
                @Json(name = "portfolio_url")
                val portfolioUrl: String?, // http://instagram.com/lilybanse
                @Json(name = "twitter_username")
                val twitterUsername: String?, // LVnatikk
                @Json(name = "paypal_email")
                val paypalEmail: String? // null
            )
        }

        @JsonClass(generateAdapter = true)
        data class Tag(
            @Json(name = "type")
            val type: String?, // landing_page
            @Json(name = "title")
            val title: String?, // food
            @Json(name = "source")
            val source: Source?
        ) {
            @JsonClass(generateAdapter = true)
            data class Source(
                @Json(name = "title")
                val title: String?, // Food images & pictures
                @Json(name = "subtitle")
                val subtitle: String?, // Download free food images
                @Json(name = "description")
                val description: String?, // Stunningly delicious street food, magnificent banquets, quiet family dinners: each is beautiful in it's own right. Unsplash captures that beauty, and lets you choose from a curated selection of the finest food images on the web (and always free).
                @Json(name = "meta_title")
                val metaTitle: String?, // 20+ Best Free Food Pictures on Unsplash
                @Json(name = "meta_description")
                val metaDescription: String? // Choose from hundreds of free food pictures. Download HD food photos for free on Unsplash.
            )
        }
    }
}