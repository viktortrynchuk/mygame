package com.example.mygame.engine_and_helpers.test_seeder

// Import your entity packages (adjust to your actual package layout)
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity
import com.example.mygame.database.politics_diplomacy_succession.CountryEntity
import com.example.mygame.database.population_and_society.NationalityEntity
import com.example.mygame.database.religion.ReligionEntity
import com.example.mygame.database.world_and_geography.RiverEntity
import com.example.mygame.database.world_and_geography.TerrainEntity

/**
 * The ONLY place you hand-author data.
 * DbSeeder:
 *  - inserts these first
 *  - reads real IDs
 *  - derives the rest so EVERY table receives a couple rows
 */
object SeedData {

    // ---- Core world ----
    val countries = listOf(
        CountryEntity(id = 0, name = "Ardania"),
        CountryEntity(id = 0, name = "Belloria")
    )

    // Lands reference countries by POSITION; DbSeeder remaps to actual country IDs
    //   index: 0..n, we attach country indexes here, seeder resolves to IDs
    data class LandProto(val name: String, val countryIndex: Int, val terrain: String)
    val landProtos = listOf(
        LandProto("Eastmarch", 0, "PLAINS"),
        LandProto("Greenwood", 0, "FOREST"),
        LandProto("Stoneford", 1, "HILLS"),
        LandProto("Harborbay", 1, "PLAINS")
    )

    // Neighbor pairs by LAND INDEX; DbSeeder remaps them to real land IDs
    val neighborPairs = listOf(
        0 to 1, // Eastmarch <-> Greenwood
        0 to 2, // Eastmarch <-> Stoneford
        1 to 3  // Greenwood <-> Harborbay
    )

    // Ownership (lands owned by country index); DbSeeder remaps
    val landOwnershipByCountryIndex = listOf(
        0 to 0, // Eastmarch -> Ardania
        1 to 0, // Greenwood -> Ardania
        2 to 1, // Stoneford -> Belloria
        3 to 1  // Harborbay -> Belloria
    )

    // ---- Peoples & identity ----
    val religions = listOf(
        ReligionEntity(id = 0, name = "Sun Cult"),
        ReligionEntity(id = 0, name = "Moon Faith")
    )
    val nationalities = listOf(
        NationalityEntity(id = 0, name = "Ardan"),
        NationalityEntity(id = 0, name = "Bellor")
    )
    data class NobleProto(val name: String, val nationalityIndex: Int, val religionIndex: Int)
    val nobles = listOf(
        NobleProto("Varek of Eastmarch", 0, 0),
        NobleProto("Ardan the Just",     0, 0),
        NobleProto("Lady Serin",         1, 1),
        NobleProto("Duke Harol",         1, 1)
    )

    // ---- Structures/Forts by land index ----
    data class StructureProto(val landIndex: Int, val type: String, val group: String)
    val structures = listOf(
        StructureProto(0, "MARKET",   "ECONOMY"),
        StructureProto(1, "BARRACKS", "MILITARY"),
        StructureProto(2, "TEMPLE",   "RELIGION"),
        StructureProto(3, "PORT",     "LOGISTICS")
    )
    data class FortProto(val landIndex: Int, val level: Int, val breached: Boolean)
    val forts = listOf(
        FortProto(0, 1, false),
        FortProto(2, 2, false)
    )

    // ---- Rivers (segments by land index; seeder remaps) ----
    val rivers = listOf(
        RiverEntity(id = 0, name = "Silverrun", sourceType = "SPRING", sinkType = "SEA"),
        RiverEntity(id = 0, name = "Greenflow", sourceType = "LAKE",   sinkType = "RIVER")
    )
    data class RiverSegProto(val riverIndex: Int, val orderIndex: Int, val landIndex: Int)
    val riverSegments = listOf(
        RiverSegProto(0, 0, 0),
        RiverSegProto(0, 1, 1),
        RiverSegProto(1, 0, 2),
        RiverSegProto(1, 1, 3)
    )

    // ---- Scenarios (so GameEngine can start) ----
    val scenarios = listOf(
        ScenarioEntity(scenarioId = 0, actorId = 1, currentLandId = 1, goal = "Expand east",
            viewNum = 1, descr = "Ardania rising", name = "Scenario A", isActive = true),
        ScenarioEntity(scenarioId = 0, actorId = 3, currentLandId = 3, goal = "Hold the bay",
            viewNum = 1, descr = "Belloria holds", name = "Scenario B", isActive = false)
    )

    // ---- Terrain codes used by lands ----
    val terrains = listOf(
        TerrainEntity(code = "PLAINS", moveCost = 1),
        TerrainEntity(code = "FOREST", moveCost = 2),
        TerrainEntity(code = "HILLS",  moveCost = 2)
    )
}