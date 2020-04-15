package `in`.nitin.firebaseimageupload.datasource.repo


data class Result<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val progress: Int?
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        PROGRESS
    }

    companion object {
        fun <T> success(data: T): Result<T> {
            return Result(
                Status.SUCCESS,
                data,
                null, null
            )
        }

        fun <T> error(message: String, data: T? = null): Result<T> {
            return Result(
                Status.ERROR,
                data,
                message, null
            )
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(
                Status.LOADING,
                data,
                null
                , null
            )

        }

        fun <T> progress(progress: Int? = null): Result<T> {
            return Result(
                Status.PROGRESS,
                null,
                null
                , progress
            )

        }
    }
}
