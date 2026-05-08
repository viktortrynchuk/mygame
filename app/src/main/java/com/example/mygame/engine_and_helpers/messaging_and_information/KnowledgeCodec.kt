package com.example.mygame.engine_and_helpers.messaging_and_information

object KnowledgeCodec {
    fun parse(line: String): Fact? = try {
        val p = line.split(":")
        when (p[0]) {
            "LAND_OWNER" -> Fact.LandOwner(p[1].toLong(), p[2], p[3].toLong())
            "LAND_SAT" -> Fact.LandSatisfaction(p[1].toLong(), p[2].toInt())
            "STRUCT" -> Fact.StructureKnown(p[1].toLong(), p[2])
            "FORT" -> Fact.FortLevel(p[1].toLong(), p[2].toInt())
            "FLOOD" -> Fact.FloodTurns(p[1].toLong(), p[2].toInt())
            "POISON" -> Fact.PoisonedUntil(p[1].toLong(), p[2].toInt())
            "MARKET" -> Fact.MarketKnown(p[1].toLong())
            "TEMPLES" -> Fact.TemplesCount(p[1].toLong(), p[2].toInt())
            "MONASTERIES" -> Fact.MonasteriesCount(p[1].toLong(), p[2].toInt())
            "ARMY_AT" -> Fact.ArmyAtLand(p[1].toLong(), p[2].toLong(), p[3].toLong())
            "ORDER_ECHO" -> Fact.OrderEcho(p[1].toLong(), p[2], p[3].toInt())
            "DIPLOMACY" -> Fact.DiplomacyStatus(p[1].toLong(), p[2].toLong(), p[3])
            "REBEL" -> Fact.RebellionSighted(p[1].toLong())
            "CROP_SHORT" -> Fact.CropShortage(p[1].toLong())
            "CRIME" -> Fact.CrimeReported(p[1].toLong(), p[2].toLong())
            "TRIAL" -> Fact.TrialOpened(p[1].toLong(), p[2].toLong())
            "VERDICT" -> Fact.VerdictKnown(p[1].toLong(), p[2] == "1")
            else -> null
        }
    } catch (_: Throwable) { null }

    fun encode(f: Fact): String = when (f) {
        is Fact.LandOwner -> "LAND_OWNER:${f.landId}:${f.ownerType}:${f.ownerRef}"
        is Fact.LandSatisfaction -> "LAND_SAT:${f.landId}:${f.level}"
        is Fact.StructureKnown -> "STRUCT:${f.landId}:${f.type}"
        is Fact.FortLevel -> "FORT:${f.landId}:${f.level}"
        is Fact.FloodTurns -> "FLOOD:${f.landId}:${f.turns}"
        is Fact.PoisonedUntil -> "POISON:${f.landId}:${f.turn}"
        is Fact.MarketKnown -> "MARKET:${f.landId}"
        is Fact.TemplesCount -> "TEMPLES:${f.landId}:${f.count}"
        is Fact.MonasteriesCount -> "MONASTERIES:${f.landId}:${f.count}"
        is Fact.ArmyAtLand -> "ARMY_AT:${f.armyId}:${f.countryId}:${f.landId}"
        is Fact.OrderEcho -> "ORDER_ECHO:${f.armyId}:${f.order}:${f.turn}"
        is Fact.DiplomacyStatus -> "DIPLOMACY:${f.a}:${f.b}:${f.status}"
        is Fact.RebellionSighted -> "REBEL:${f.landId}"
        is Fact.CropShortage -> "CROP_SHORT:${f.landId}"
        is Fact.CrimeReported -> "CRIME:${f.landId}:${f.crimeId}"
        is Fact.TrialOpened -> "TRIAL:${f.crimeId}:${f.trialId}"
        is Fact.VerdictKnown -> "VERDICT:${f.trialId}:${if (f.guilty) 1 else 0}"
    }
}