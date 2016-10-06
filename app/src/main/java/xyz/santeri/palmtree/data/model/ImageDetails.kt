package xyz.santeri.palmtree.data.model

import android.os.Parcelable
import com.google.auto.value.AutoValue

/**
 * @author Santeri Elo
 */
@AutoValue
abstract class ImageDetails : Parcelable {
    /**
     * @return File id, e.g. 79085
     */
    abstract fun id(): Int

    /**
     * @return File URL
     */
    abstract fun fileUrl(): String

    /**
     * @return [ImageType]
     */
    abstract fun type(): ImageType

    /**
     * @return True if image is NSFW (not safe for work)
     */
    abstract fun nsfw(): Boolean

    /**
     * @return File title
     */
    abstract fun title(): String

    /**
     * @return File rating, e.g. +123
     */
    abstract fun rating(): String?  // Don't need it as an integer

    /**
     * @return File description
     */
    abstract fun description(): String?

    /**
     * @return Metadata, e.g. sender & date
     */
    abstract fun metadata(): String?

    companion object {
        @JvmStatic
        fun create(id: Int,
                   fileUrl: String,
                   type: ImageType,
                   nsfw: Boolean,
                   title: String,
                   rating: String?,
                   description: String?,
                   metadata: String?): ImageDetails {
            return AutoValue_ImageDetails(id, fileUrl, type, nsfw, title, rating, description, metadata)
        }
    }
}

enum class ImageType {
    IMAGE,
    VIDEO,
    UNDEFINED
}
