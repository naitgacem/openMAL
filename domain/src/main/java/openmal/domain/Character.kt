package openmal.domain

data class Character(
    val id: Int,
    val name: String,
    val imageURL: String? = null,
    val description: String? = null,
    val gender: Gender = Gender.Unknown,
    val age: String? = null
)