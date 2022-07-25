package com.example.mapwithtab.model

import java.util.*

data class CountryModel(
    val capital: List<String>?,
    val flag: String = "",
    val independent: Boolean = false,
    val landlocked: Boolean = false,
    val postalCode: PostalCode,
    val flags: Flags,
    val capitalInfo: CapitalInfo,
    val ccn: String = "",
    val coatOfArms: CoatOfArms,
    val demonyms: Demonyms,
    val car: Car,
    val translations: Translations,
    val altSpellings: List<String>?,
    val area: Number = 0,
    val languages: Languages,
    val maps: Maps,
    val subregion: String = "",
    val idd: Idd,
    val tld: List<String>?,
    val unMember: Boolean = false,
    val continents: List<String>?,
    val population: Number = 0,
    val startOfWeek: String = "",
    val timezones: List<String>?,
    val name: Name,
    val cca: String = "",
    val region: String = "",
    val latlng: List<Number>?,
    val status: String = "",
    val currencies: Any,
    var isExpand: Boolean = false
)