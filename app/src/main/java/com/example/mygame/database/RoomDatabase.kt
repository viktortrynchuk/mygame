package com.example.mygame.database

import android.content.Context
import androidx.room.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mygame.dao.ActorDAO
import com.example.mygame.dao.ActorLocationDAO
import com.example.mygame.dao.persistence_and_game_state.ScenarioDao
import com.example.mygame.dao.SessionDAO
import com.example.mygame.dao.armies_units_warfare.AmmoStockDao
import com.example.mygame.dao.armies_units_warfare.ArmyDao
import com.example.mygame.dao.armies_units_warfare.BattleDao
import com.example.mygame.dao.armies_units_warfare.BattleParticipantDao
import com.example.mygame.dao.armies_units_warfare.BattleTurnDao
import com.example.mygame.dao.armies_units_warfare.DesertionDao
import com.example.mygame.dao.armies_units_warfare.EquipmentStockDao
import com.example.mygame.dao.armies_units_warfare.FlagDao
import com.example.mygame.dao.armies_units_warfare.LootDao
import com.example.mygame.dao.armies_units_warfare.MilitaryOrderDao
import com.example.mygame.dao.armies_units_warfare.MoraleDao
import com.example.mygame.dao.armies_units_warfare.SiegeDao
import com.example.mygame.dao.armies_units_warfare.SignalChannelDao
import com.example.mygame.dao.armies_units_warfare.TacticDao
import com.example.mygame.dao.armies_units_warfare.UnitCompositionDao
import com.example.mygame.dao.armies_units_warfare.UnitDao
import com.example.mygame.dao.dignity_duels_conflicts.AssassinationDao
import com.example.mygame.dao.dignity_duels_conflicts.ConflictDao
import com.example.mygame.dao.dignity_duels_conflicts.DuelBanDao
import com.example.mygame.dao.dignity_duels_conflicts.DuelDao
import com.example.mygame.dao.dignity_duels_conflicts.DuelEventDao
import com.example.mygame.dao.dignity_duels_conflicts.HonorLogDao
import com.example.mygame.dao.dignity_duels_conflicts.OffenseDao
import com.example.mygame.dao.dignity_duels_conflicts.PersonalConflictDao
import com.example.mygame.dao.economy_resources_trade.CraftOrderDao
import com.example.mygame.dao.economy_resources_trade.EmbargoDao
import com.example.mygame.dao.economy_resources_trade.FarmDao
import com.example.mygame.dao.economy_resources_trade.FisheryDao
import com.example.mygame.dao.economy_resources_trade.GuildDao
import com.example.mygame.dao.economy_resources_trade.LossTimerDao
import com.example.mygame.dao.economy_resources_trade.ManifestDao
import com.example.mygame.dao.economy_resources_trade.MarketDao
import com.example.mygame.dao.economy_resources_trade.MarketStockDao
import com.example.mygame.dao.economy_resources_trade.MineDao
import com.example.mygame.dao.economy_resources_trade.MintOrderDao
import com.example.mygame.dao.economy_resources_trade.PriceDao
import com.example.mygame.dao.economy_resources_trade.RecipeDao
import com.example.mygame.dao.economy_resources_trade.ResourceProductionDao
import com.example.mygame.dao.economy_resources_trade.ResourceStockDao
import com.example.mygame.dao.economy_resources_trade.SawmillDao
import com.example.mygame.dao.economy_resources_trade.StockDao
import com.example.mygame.dao.economy_resources_trade.TaxCollectionDao
import com.example.mygame.dao.economy_resources_trade.TaxPolicyDao
import com.example.mygame.dao.economy_resources_trade.TradeAgreementDao
import com.example.mygame.dao.economy_resources_trade.TradeRouteDao
import com.example.mygame.dao.economy_resources_trade.TreasuryDao
import com.example.mygame.dao.economy_resources_trade.WarehouseDao
import com.example.mygame.dao.entertainment_and_social.BallDao
import com.example.mygame.dao.entertainment_and_social.FestivalDao
import com.example.mygame.dao.entertainment_and_social.FestivalEventDao
import com.example.mygame.dao.entertainment_and_social.GossipDao
import com.example.mygame.dao.entertainment_and_social.HuntEventDao
import com.example.mygame.dao.entertainment_and_social.PerformanceDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallAlcoholDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallBardDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallGuestDao
import com.example.mygame.dao.entertainment_and_social.PoisonAttemptDao
import com.example.mygame.dao.entertainment_and_social.TournamentDao
import com.example.mygame.dao.foundations_core.AuditLogDao
import com.example.mygame.dao.foundations_core.IntegrityDao
import com.example.mygame.dao.foundations_core.RuleOverrideDao
import com.example.mygame.dao.foundations_core.TurnClockDao
import com.example.mygame.dao.justice_and_court.CaseDao
import com.example.mygame.dao.justice_and_court.CourtDao
import com.example.mygame.dao.justice_and_court.CrimeDao
import com.example.mygame.dao.justice_and_court.EvidenceDao
import com.example.mygame.dao.justice_and_court.PunishmentDao
import com.example.mygame.dao.justice_and_court.TrialDao
import com.example.mygame.dao.justice_and_court.VerdictDao
import com.example.mygame.dao.messaging_and_information.DoveDao
import com.example.mygame.dao.messaging_and_information.InterceptionDao
import com.example.mygame.dao.messaging_and_information.KnowledgeDao
import com.example.mygame.dao.messaging_and_information.MessageDao
import com.example.mygame.dao.messaging_and_information.MessengerDao
import com.example.mygame.dao.messaging_and_information.PostOfficeDao
import com.example.mygame.dao.messaging_and_information.SealDao
import com.example.mygame.dao.movements_logistics_supplies.BoatDao
import com.example.mygame.dao.movements_logistics_supplies.ConvoyDao
import com.example.mygame.dao.movements_logistics_supplies.FatigueStateDao
import com.example.mygame.dao.movements_logistics_supplies.HazardDao
import com.example.mygame.dao.movements_logistics_supplies.LogisticsLogDao
import com.example.mygame.dao.movements_logistics_supplies.MoraleHitDao
import com.example.mygame.dao.movements_logistics_supplies.MovementOrderDao
import com.example.mygame.dao.movements_logistics_supplies.PathSegmentDao
import com.example.mygame.dao.movements_logistics_supplies.RationPlanDao
import com.example.mygame.dao.movements_logistics_supplies.RequisitionDao
import com.example.mygame.dao.movements_logistics_supplies.RouteDao
import com.example.mygame.dao.movements_logistics_supplies.RouteStepDao
import com.example.mygame.dao.movements_logistics_supplies.SecretRouteDao
import com.example.mygame.dao.movements_logistics_supplies.ShipDao
import com.example.mygame.dao.movements_logistics_supplies.SupplyDepotDao
import com.example.mygame.dao.movements_logistics_supplies.SupplyLineDao
import com.example.mygame.dao.movements_logistics_supplies.WagonDao
import com.example.mygame.dao.movements_logistics_supplies.WaterSourceDao
import com.example.mygame.dao.nobility_titles_and_court.CourtExpenseDao
import com.example.mygame.dao.nobility_titles_and_court.CourtMembershipDao
import com.example.mygame.dao.nobility_titles_and_court.CourtPositionDao
import com.example.mygame.dao.nobility_titles_and_court.FamilyDao
import com.example.mygame.dao.nobility_titles_and_court.FavoriteDao
import com.example.mygame.dao.nobility_titles_and_court.NobleDao
import com.example.mygame.dao.nobility_titles_and_court.NobleTitleDao
import com.example.mygame.dao.nobility_titles_and_court.PositionDao
import com.example.mygame.dao.nobility_titles_and_court.PrestigeDao
import com.example.mygame.dao.nobility_titles_and_court.RespectDao
import com.example.mygame.dao.nobility_titles_and_court.TitleDao
import com.example.mygame.dao.nobility_titles_and_court.TitleTrackDao
import com.example.mygame.dao.nobility_titles_and_court.TraitDao
import com.example.mygame.dao.persistence_and_game_state.ScenarioGoalDao
import com.example.mygame.dao.persistence_and_game_state.WorldCommitDao
import com.example.mygame.dao.politics_diplomacy_succession.AmbassadorDao
import com.example.mygame.dao.politics_diplomacy_succession.CasusBelliDao
import com.example.mygame.dao.politics_diplomacy_succession.CountryDao
import com.example.mygame.dao.politics_diplomacy_succession.DiplomacyDao
import com.example.mygame.dao.politics_diplomacy_succession.MarriageDao
import com.example.mygame.dao.politics_diplomacy_succession.SuccessionDao
import com.example.mygame.dao.population_and_society.HouseholdDao
import com.example.mygame.dao.population_and_society.MayorDao
import com.example.mygame.dao.population_and_society.MayorOrderDao
import com.example.mygame.dao.population_and_society.NationalityDao
import com.example.mygame.dao.population_and_society.PopulationDao
import com.example.mygame.dao.population_and_society.SatisfactionDao
import com.example.mygame.dao.population_and_society.WorkDao
import com.example.mygame.dao.rebellion_banditry.BanditArmyDao
import com.example.mygame.dao.rebellion_banditry.BanditGroupDao
import com.example.mygame.dao.rebellion_banditry.OutlawDao
import com.example.mygame.dao.rebellion_banditry.RebelArmyDao
import com.example.mygame.dao.rebellion_banditry.RebellionDao
import com.example.mygame.dao.rebellion_banditry.RevoltDao
import com.example.mygame.dao.rebellion_banditry.SpreadEventDao
import com.example.mygame.dao.rebellion_banditry.SuppressionLogDao
import com.example.mygame.dao.religion.CelibacyRuleDao
import com.example.mygame.dao.religion.ConversionDao
import com.example.mygame.dao.religion.LeaderDao
import com.example.mygame.dao.religion.MonasteryDao
import com.example.mygame.dao.religion.MonkDao
import com.example.mygame.dao.religion.OppressionDao
import com.example.mygame.dao.religion.PriestDao
import com.example.mygame.dao.religion.RankDao
import com.example.mygame.dao.religion.ReligionDao
import com.example.mygame.dao.religion.ReligiousClashDao
import com.example.mygame.dao.religion.TempleDao
import com.example.mygame.dao.religion.ToleranceDao
import com.example.mygame.dao.roles_and_offices.BudgetRequestDao
import com.example.mygame.dao.roles_and_offices.ChancellorDao
import com.example.mygame.dao.roles_and_offices.OfficeAssignmentDao
import com.example.mygame.dao.roles_and_offices.OfficeDao
import com.example.mygame.dao.roles_and_offices.RoleAssignmentDao
import com.example.mygame.dao.roles_and_offices.RoleDao
import com.example.mygame.dao.world_and_geography.BuildOrderDao
import com.example.mygame.dao.world_and_geography.CrossingDao
import com.example.mygame.dao.world_and_geography.DamDao
import com.example.mygame.dao.world_and_geography.FloodDao
import com.example.mygame.dao.world_and_geography.FortificationDao
import com.example.mygame.dao.world_and_geography.LandDao
import com.example.mygame.dao.world_and_geography.NeighborDao
import com.example.mygame.dao.world_and_geography.OwnershipDao
import com.example.mygame.dao.world_and_geography.RiverDao
import com.example.mygame.dao.world_and_geography.RiverSegmentDao
import com.example.mygame.dao.world_and_geography.SiegeworkDao
import com.example.mygame.dao.world_and_geography.StructureDao
import com.example.mygame.dao.world_and_geography.StructureEffectDao
import com.example.mygame.dao.world_and_geography.StructureProgressDao
import com.example.mygame.dao.world_and_geography.TerrainDao
import com.example.mygame.dao.world_and_geography.WaterPoisonDao
import com.example.mygame.database.armies_units_warfare.AmmoStockEntity
import com.example.mygame.database.armies_units_warfare.ArmyEntity
import com.example.mygame.database.armies_units_warfare.BattleEntity
import com.example.mygame.database.armies_units_warfare.BattleParticipantEntity
import com.example.mygame.database.armies_units_warfare.BattleTurnEntity
import com.example.mygame.database.armies_units_warfare.BulletCraftOrderEntity
import com.example.mygame.database.armies_units_warfare.CasualtyEntity
import com.example.mygame.database.armies_units_warfare.ContractEntity
import com.example.mygame.database.armies_units_warfare.DesertionEventEntity
import com.example.mygame.database.armies_units_warfare.EquipmentStockEntity
import com.example.mygame.database.armies_units_warfare.FlagEntity
import com.example.mygame.database.armies_units_warfare.LootEntity
import com.example.mygame.database.armies_units_warfare.MercPoolEntity
import com.example.mygame.database.armies_units_warfare.MilitaryOrderEntity
import com.example.mygame.database.armies_units_warfare.MoraleEntity
import com.example.mygame.database.armies_units_warfare.PrisonerEntity
import com.example.mygame.database.armies_units_warfare.SiegeEntity
import com.example.mygame.database.armies_units_warfare.SignalChannelEntity
import com.example.mygame.database.armies_units_warfare.TacticEntity
import com.example.mygame.database.armies_units_warfare.UnitCompositionEntity
import com.example.mygame.database.armies_units_warfare.UnitEntity
import com.example.mygame.database.dignity_duels_conflicts.AssassinationContractEntity
import com.example.mygame.database.dignity_duels_conflicts.DuelBanEntity
import com.example.mygame.database.dignity_duels_conflicts.DuelEntity
import com.example.mygame.database.dignity_duels_conflicts.DuelEventEntity
import com.example.mygame.database.dignity_duels_conflicts.HonorLogEntity
import com.example.mygame.database.dignity_duels_conflicts.OffenseEntity
import com.example.mygame.database.dignity_duels_conflicts.PersonalConflictEntity
import com.example.mygame.database.economy_resources_trade.CraftOrderEntity
import com.example.mygame.database.economy_resources_trade.DepositDepletionEntity
import com.example.mygame.database.economy_resources_trade.EmbargoEntity
import com.example.mygame.database.economy_resources_trade.FarmEntity
import com.example.mygame.database.economy_resources_trade.FisheryEntity
import com.example.mygame.database.economy_resources_trade.GuildEntity
import com.example.mygame.database.economy_resources_trade.LossTimerEntity
import com.example.mygame.database.economy_resources_trade.ManifestLineEntity
import com.example.mygame.database.economy_resources_trade.MarketEntity
import com.example.mygame.database.economy_resources_trade.MarketStockEntity
import com.example.mygame.database.economy_resources_trade.MerchantAssignmentEntity
import com.example.mygame.database.economy_resources_trade.MineEntity
import com.example.mygame.database.economy_resources_trade.MintOrderEntity
import com.example.mygame.database.economy_resources_trade.PriceEntity
import com.example.mygame.database.economy_resources_trade.RecipeEntity
import com.example.mygame.database.economy_resources_trade.ReforestationDebtEntity
import com.example.mygame.database.economy_resources_trade.ResourceProductionEntity
import com.example.mygame.database.economy_resources_trade.ResourceStockEntity
import com.example.mygame.database.economy_resources_trade.SawmillEntity
import com.example.mygame.database.economy_resources_trade.StockEntity
import com.example.mygame.database.economy_resources_trade.TaxCollectionEntity
import com.example.mygame.database.economy_resources_trade.TaxPolicyEntity
import com.example.mygame.database.economy_resources_trade.TradeAgreementEntity
import com.example.mygame.database.economy_resources_trade.TradeRouteEntity
import com.example.mygame.database.economy_resources_trade.TreasuryEntity
import com.example.mygame.database.economy_resources_trade.WarehouseEntity
import com.example.mygame.database.entertainment_and_social.BallEventEntity
import com.example.mygame.database.entertainment_and_social.FestivalEntity
import com.example.mygame.database.entertainment_and_social.FestivalEventEntity
import com.example.mygame.database.entertainment_and_social.GossipEntity
import com.example.mygame.database.entertainment_and_social.HuntEventEntity
import com.example.mygame.database.entertainment_and_social.PerformanceEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallAlcoholEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallBardEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallGuestEntity
import com.example.mygame.database.entertainment_and_social.PoisonAttemptEntity
import com.example.mygame.database.entertainment_and_social.TournamentEntity
import com.example.mygame.database.foundations_core.AuditLogEntity
import com.example.mygame.database.foundations_core.IntegrityViolationEntity
import com.example.mygame.database.foundations_core.RuleOverrideEntity
import com.example.mygame.database.foundations_core.TurnClockEntity
import com.example.mygame.database.justice_and_court.CaseEntity
import com.example.mygame.database.justice_and_court.CourtEntity
import com.example.mygame.database.justice_and_court.CrimeEntity
import com.example.mygame.database.justice_and_court.EvidenceEntity
import com.example.mygame.database.justice_and_court.PunishmentEntity
import com.example.mygame.database.justice_and_court.TrialEntity
import com.example.mygame.database.justice_and_court.VerdictEntity
import com.example.mygame.database.messaging_and_information.DoveEntity
import com.example.mygame.database.messaging_and_information.InterceptionLogEntity
import com.example.mygame.database.messaging_and_information.KnowledgeEntryEntity
import com.example.mygame.database.messaging_and_information.MessageEntity
import com.example.mygame.database.messaging_and_information.MessengerEntity
import com.example.mygame.database.messaging_and_information.PostOfficeEntity
import com.example.mygame.database.messaging_and_information.SealEntity
import com.example.mygame.database.movements_logistics_supplies.AmbushMarkerEntity
import com.example.mygame.database.movements_logistics_supplies.BoatEntity
import com.example.mygame.database.movements_logistics_supplies.ConvoyEntity
import com.example.mygame.database.movements_logistics_supplies.CrossingReservationEntity
import com.example.mygame.database.movements_logistics_supplies.EscortAssignmentEntity
import com.example.mygame.database.movements_logistics_supplies.FatigueStateEntity
import com.example.mygame.database.movements_logistics_supplies.HazardEntity
import com.example.mygame.database.movements_logistics_supplies.LogisticsLogEntity
import com.example.mygame.database.movements_logistics_supplies.MoraleHitEntity
import com.example.mygame.database.movements_logistics_supplies.MovementOrderEntity
import com.example.mygame.database.movements_logistics_supplies.PathSegmentEntity
import com.example.mygame.database.movements_logistics_supplies.PoisonEventEntity
import com.example.mygame.database.movements_logistics_supplies.RationPlanEntity
import com.example.mygame.database.movements_logistics_supplies.RequisitionEventEntity
import com.example.mygame.database.movements_logistics_supplies.RouteEntity
import com.example.mygame.database.movements_logistics_supplies.RouteStepEntity
import com.example.mygame.database.movements_logistics_supplies.SecretRouteEntity
import com.example.mygame.database.movements_logistics_supplies.ShipEntity
import com.example.mygame.database.movements_logistics_supplies.SupplyDepotEntity
import com.example.mygame.database.movements_logistics_supplies.SupplyLineEntity
import com.example.mygame.database.movements_logistics_supplies.WagonEntity
import com.example.mygame.database.movements_logistics_supplies.WaterSourceEntity
import com.example.mygame.database.nobility_titles_and_court.CourtExpenseEntity
import com.example.mygame.database.nobility_titles_and_court.CourtMembershipEntity
import com.example.mygame.database.nobility_titles_and_court.CourtPositionEntity
import com.example.mygame.database.nobility_titles_and_court.FamilyLinkEntity
import com.example.mygame.database.nobility_titles_and_court.FavoriteFlag
import com.example.mygame.database.nobility_titles_and_court.NobleEntity
import com.example.mygame.database.nobility_titles_and_court.NobleTitleEntity
import com.example.mygame.database.nobility_titles_and_court.PrestigeLogEntity
import com.example.mygame.database.nobility_titles_and_court.RespectFearEntity
import com.example.mygame.database.nobility_titles_and_court.TitleEntity
import com.example.mygame.database.nobility_titles_and_court.TitleTrackEntity
import com.example.mygame.database.nobility_titles_and_court.TraitEntity
import com.example.mygame.database.persistence_and_game_state.CurrentSession
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity
import com.example.mygame.database.persistence_and_game_state.ScenarioGoalEntity
import com.example.mygame.database.persistence_and_game_state.WorldCommitEntity
import com.example.mygame.database.politics_diplomacy_succession.AmbassadorEntity
import com.example.mygame.database.politics_diplomacy_succession.CasusBelliEntity
import com.example.mygame.database.politics_diplomacy_succession.CountryEntity
import com.example.mygame.database.politics_diplomacy_succession.DiplomaticStatusEntity
import com.example.mygame.database.politics_diplomacy_succession.MarriageProposalEntity
import com.example.mygame.database.politics_diplomacy_succession.SuccessionLinkEntity
import com.example.mygame.database.population_and_society.HouseholdEntity
import com.example.mygame.database.population_and_society.MayorEntity
import com.example.mygame.database.population_and_society.MayorOrderEntity
import com.example.mygame.database.population_and_society.NationalityEntity
import com.example.mygame.database.population_and_society.PopulationStatEntity
import com.example.mygame.database.population_and_society.SatisfactionEntity
import com.example.mygame.database.population_and_society.WorkAssignmentEntity
import com.example.mygame.database.rebellion_banditry.BanditArmyEntity
import com.example.mygame.database.rebellion_banditry.BanditGroupEntity
import com.example.mygame.database.rebellion_banditry.OutlawEntity
import com.example.mygame.database.rebellion_banditry.RebelArmyEntity
import com.example.mygame.database.rebellion_banditry.RebellionEntity
import com.example.mygame.database.rebellion_banditry.RevoltEntity
import com.example.mygame.database.rebellion_banditry.SpreadEventEntity
import com.example.mygame.database.rebellion_banditry.SuppressionLogEntity
import com.example.mygame.database.religion.CelibacyRuleEntity
import com.example.mygame.database.religion.ConversionTaskEntity
import com.example.mygame.database.religion.LeaderEntity
import com.example.mygame.database.religion.MonasteryEntity
import com.example.mygame.database.religion.MonkEntity
import com.example.mygame.database.religion.OppressionStateEntity
import com.example.mygame.database.religion.PriestEntity
import com.example.mygame.database.religion.ReligionEntity
import com.example.mygame.database.religion.ReligionRankEntity
import com.example.mygame.database.religion.ReligiousClashLogEntity
import com.example.mygame.database.religion.TempleEntity
import com.example.mygame.database.religion.ToleranceMatrixEntity
import com.example.mygame.database.roles_and_offices.BudgetRequestEntity
import com.example.mygame.database.roles_and_offices.ChancellorEntity
import com.example.mygame.database.roles_and_offices.CoinHolderEntity
import com.example.mygame.database.roles_and_offices.DefenseCommanderEntity
import com.example.mygame.database.roles_and_offices.OfficeAssignmentEntity
import com.example.mygame.database.roles_and_offices.OfficeEntity
import com.example.mygame.database.roles_and_offices.RoleAssignmentEntity
import com.example.mygame.database.roles_and_offices.RoleEntity
import com.example.mygame.database.world_and_geography.BuildOrderEntity
import com.example.mygame.database.world_and_geography.CrossingEntity
import com.example.mygame.database.world_and_geography.DamEntity
import com.example.mygame.database.world_and_geography.FloodStateEntity
import com.example.mygame.database.world_and_geography.FortificationEntity
import com.example.mygame.database.world_and_geography.LandEntity
import com.example.mygame.database.world_and_geography.NeighborEntity
import com.example.mygame.database.world_and_geography.OwnershipEntity
import com.example.mygame.database.world_and_geography.RiverEntity
import com.example.mygame.database.world_and_geography.RiverSegmentEntity
import com.example.mygame.database.world_and_geography.SiegeworkEntity
import com.example.mygame.database.world_and_geography.StructureEffectEntity
import com.example.mygame.database.world_and_geography.StructureEntity
import com.example.mygame.database.world_and_geography.StructureProgressEntity
import com.example.mygame.database.world_and_geography.TerrainEntity
import com.example.mygame.database.world_and_geography.WaterPoisonStateEntity

@Database(entities = [  CurrentSession::class,
    ScenarioEntity::class,
    Actor::class,
    ActorLocation::class,
    AmmoStockEntity::class,
    ArmyEntity::class,
    BattleEntity::class,
    BattleParticipantEntity::class,
    BattleTurnEntity::class,
    BulletCraftOrderEntity::class,
    CasualtyEntity::class,
    ContractEntity::class,
    DesertionEventEntity::class,
    EquipmentStockEntity::class,
    FlagEntity::class,
    LootEntity::class,
    MercPoolEntity::class,
    MilitaryOrderEntity::class,
    MoraleEntity::class,
    PrisonerEntity::class,
    SiegeEntity::class,
    SignalChannelEntity::class,
    TacticEntity::class,
    UnitCompositionEntity::class,
    UnitEntity::class,
    AssassinationContractEntity::class,
    DuelBanEntity::class,
    DuelEntity::class,
    DuelEventEntity::class,
    HonorLogEntity::class,
    OffenseEntity::class,
    PersonalConflictEntity::class,
    CraftOrderEntity::class,
    DepositDepletionEntity::class,
    EmbargoEntity::class,
    FarmEntity::class,
    FisheryEntity::class,
    GuildEntity::class,
    LossTimerEntity::class,
    ManifestLineEntity::class,
    MarketEntity::class,
    MarketStockEntity::class,
    MerchantAssignmentEntity::class,
    MineEntity::class,
    MintOrderEntity::class,
    PriceEntity::class,
    RecipeEntity::class,
    ReforestationDebtEntity::class,
    ResourceProductionEntity::class,
    ResourceStockEntity::class,
    SawmillEntity::class,
    StockEntity::class,
    TaxCollectionEntity::class,
    TaxPolicyEntity::class,
    TradeAgreementEntity::class,
    TradeRouteEntity::class,
    TreasuryEntity::class,
    WarehouseEntity::class,
    BallEventEntity::class,
    FestivalEntity::class,
    FestivalEventEntity::class,
    GossipEntity::class,
    HuntEventEntity::class,
    PerformanceEntity::class,
    PoisonAttemptEntity::class,
    TournamentEntity::class,
    AuditLogEntity::class,
    IntegrityViolationEntity::class,
    RuleOverrideEntity::class,
    TurnClockEntity::class,
    CaseEntity::class,
    CourtEntity::class,
    CrimeEntity::class,
    EvidenceEntity::class,
    PunishmentEntity::class,
    TrialEntity::class,
    VerdictEntity::class,
    DoveEntity::class,
    InterceptionLogEntity::class,
    KnowledgeEntryEntity::class,
    MessageEntity::class,
    MessengerEntity::class,
    PostOfficeEntity::class,
    SealEntity::class,
    AmbushMarkerEntity::class,
    BoatEntity::class,
    ConvoyEntity::class,
    CrossingReservationEntity::class,
    EscortAssignmentEntity::class,
    FatigueStateEntity::class,
    HazardEntity::class,
    LogisticsLogEntity::class,
    MoraleHitEntity::class,
    MovementOrderEntity::class,
    PathSegmentEntity::class,
    PoisonEventEntity::class,
    RationPlanEntity::class,
    RequisitionEventEntity::class,
    RouteEntity::class,
    RouteStepEntity::class,
    SecretRouteEntity::class,
    ShipEntity::class,
    SupplyDepotEntity::class,
    SupplyLineEntity::class,
    WagonEntity::class,
    WaterSourceEntity::class,
    CourtExpenseEntity::class,
    CourtMembershipEntity::class,
    CourtPositionEntity::class,
    FamilyLinkEntity::class,
    FavoriteFlag::class,
    NobleEntity::class,
    NobleTitleEntity::class,
    PrestigeLogEntity::class,
    RespectFearEntity::class,
    TitleEntity::class,
    TitleTrackEntity::class,
    TraitEntity::class,
    ScenarioGoalEntity::class,
    WorldCommitEntity::class,
    AmbassadorEntity::class,
    CasusBelliEntity::class,
    CountryEntity::class,
    DiplomaticStatusEntity::class,
    MarriageProposalEntity::class,
    SuccessionLinkEntity::class,
    HouseholdEntity::class,
    MayorEntity::class,
    MayorOrderEntity::class,
    NationalityEntity::class,
    PopulationStatEntity::class,
    SatisfactionEntity::class,
    WorkAssignmentEntity::class,
    BanditArmyEntity::class,
    BanditGroupEntity::class,
    OutlawEntity::class,
    RebelArmyEntity::class,
    RebellionEntity::class,
    RevoltEntity::class,
    SpreadEventEntity::class,
    SuppressionLogEntity::class,
    CelibacyRuleEntity::class,
    ConversionTaskEntity::class,
    LeaderEntity::class,
    MonasteryEntity::class,
    MonkEntity::class,
    OppressionStateEntity::class,
    PriestEntity::class,
    ReligionEntity::class,
    ReligionRankEntity::class,
    ReligiousClashLogEntity::class,
    TempleEntity::class,
    ToleranceMatrixEntity::class,
    BudgetRequestEntity::class,
    ChancellorEntity::class,
    CoinHolderEntity::class,
    DefenseCommanderEntity::class,
    OfficeAssignmentEntity::class,
    OfficeEntity::class,
    RoleAssignmentEntity::class,
    RoleEntity::class,
    BuildOrderEntity::class,
    CrossingEntity::class,
    DamEntity::class,
    FloodStateEntity::class,
    FortificationEntity::class,
    LandEntity::class,
    NeighborEntity::class,
    OwnershipEntity::class,
    RiverEntity::class,
    RiverSegmentEntity::class,
    SiegeworkEntity::class,
    StructureEffectEntity::class,
    StructureEntity::class,
    StructureProgressEntity::class,
    TerrainEntity::class,
    WaterPoisonStateEntity::class,
    PlannedBallEntity::class,
    PlannedBallBardEntity::class,
    PlannedBallGuestEntity::class,
    PlannedBallAlcoholEntity::class
], version = 33)
abstract class AppDatabase : RoomDatabase() {
    abstract fun PlannedBallAlcoholDao(): PlannedBallAlcoholDao
    abstract fun PlannedBallGuestDao(): PlannedBallGuestDao
    abstract fun PlannedBallBardDao(): PlannedBallBardDao
    abstract fun PlannedBallDao(): PlannedBallDao
    abstract fun CourtMembershipDao(): CourtMembershipDao
    abstract fun UnitCompositionDao(): UnitCompositionDao
    abstract fun BattleTurnDao(): BattleTurnDao
    abstract fun BudgetRequestDao(): BudgetRequestDao
    abstract fun SpreadEventDao(): SpreadEventDao
    abstract fun RevoltDao(): RevoltDao
    abstract fun GossipDao(): GossipDao
    abstract fun PoisonAttemptDao(): PoisonAttemptDao
    abstract fun HuntEventDao(): HuntEventDao
    abstract fun BallDao(): BallDao
    abstract fun FestivalEventDao(): FestivalEventDao
    abstract fun RouteStepDao(): RouteStepDao
    abstract fun RouteDao(): RouteDao
    abstract fun SecretRouteDao(): SecretRouteDao
    abstract fun RequisitionDao(): RequisitionDao
    abstract fun MoraleHitDao(): MoraleHitDao
    abstract fun FatigueStateDao(): FatigueStateDao
    abstract fun RationPlanDao(): RationPlanDao
    abstract fun WagonDao(): WagonDao
    abstract fun ConvoyDao(): ConvoyDao
    abstract fun ShipDao(): ShipDao
    abstract fun BoatDao(): BoatDao
    abstract fun HazardDao(): HazardDao
    abstract fun WaterSourceDao(): WaterSourceDao
    abstract fun CourtPositionDao(): CourtPositionDao
    abstract fun TitleTrackDao(): TitleTrackDao
    abstract fun ChancellorDao(): ChancellorDao
    abstract fun GuildDao(): GuildDao
    abstract fun OppressionDao(): OppressionDao
    abstract fun AssassinationDao(): AssassinationDao
    abstract fun FlagDao(): FlagDao
    abstract fun DuelEventDao(): DuelEventDao
    abstract fun DuelBanDao(): DuelBanDao
    abstract fun PersonalConflictDao(): PersonalConflictDao
    abstract fun OffenseDao(): OffenseDao
    abstract fun EvidenceDao(): EvidenceDao
    abstract fun CaseDao(): CaseDao
    abstract fun BanditArmyDao(): BanditArmyDao
    abstract fun DesertionDao(): DesertionDao
    abstract fun TacticDao(): TacticDao
    abstract fun LootDao(): LootDao
    abstract fun SignalChannelDao(): SignalChannelDao
    abstract fun EquipmentStockDao(): EquipmentStockDao
    abstract fun MoraleDao(): MoraleDao
    abstract fun EmbargoDao(): EmbargoDao
    abstract fun ManifestDao(): ManifestDao
    abstract fun LossTimerDao(): LossTimerDao
    abstract fun TaxCollectionDao(): TaxCollectionDao
    abstract fun TaxPolicyDao(): TaxPolicyDao
    abstract fun MineDao(): MineDao
    abstract fun FisheryDao(): FisheryDao
    abstract fun SawmillDao(): SawmillDao
    abstract fun FarmDao(): FarmDao
    abstract fun WarehouseDao(): WarehouseDao
    abstract fun StockDao(): StockDao
    abstract fun CraftOrderDao(): CraftOrderDao
    abstract fun MintOrderDao(): MintOrderDao
    abstract fun RecipeDao(): RecipeDao
    abstract fun sessionDAO(): SessionDAO
    abstract fun scenarioDao(): ScenarioDao
    abstract fun actorDAO(): ActorDAO
    abstract fun actorLocationDAO(): ActorLocationDAO
    abstract fun ArmyDao(): ArmyDao
    abstract fun BattleDao(): BattleDao
    abstract fun BattleParticipantDao(): BattleParticipantDao
    abstract fun MilitaryOrderDao(): MilitaryOrderDao
    abstract fun SiegeDao(): SiegeDao
    abstract fun NeighborDao(): NeighborDao
    abstract fun AmmoStockDao(): AmmoStockDao
    abstract fun TreasuryDao(): TreasuryDao
    abstract fun MarketStockDao(): MarketStockDao
    abstract fun PriceDao(): PriceDao
    abstract fun UnitDao(): UnitDao
    abstract fun ConflictDao(): ConflictDao
    abstract fun DuelDao(): DuelDao
    abstract fun HonorLogDao(): HonorLogDao
    abstract fun MarketDao(): MarketDao
    abstract fun ResourceProductionDao(): ResourceProductionDao
    abstract fun ResourceStockDao(): ResourceStockDao
    abstract fun TradeAgreementDao(): TradeAgreementDao
    abstract fun TradeRouteDao(): TradeRouteDao
    abstract fun FestivalDao(): FestivalDao
    abstract fun PerformanceDao(): PerformanceDao
    abstract fun TournamentDao(): TournamentDao
    abstract fun AuditLogDao(): AuditLogDao
    abstract fun IntegrityDao(): IntegrityDao
    abstract fun RuleOverrideDao(): RuleOverrideDao
    abstract fun TurnClockDao(): TurnClockDao
    abstract fun CrimeDao(): CrimeDao
    abstract fun PunishmentDao(): PunishmentDao
    abstract fun TrialDao(): TrialDao
    abstract fun VerdictDao(): VerdictDao
    abstract fun DoveDao(): DoveDao
    abstract fun InterceptionDao(): InterceptionDao
    abstract fun KnowledgeDao(): KnowledgeDao
    abstract fun MessageDao(): MessageDao
    abstract fun MessengerDao(): MessengerDao
    abstract fun PostOfficeDao(): PostOfficeDao
    abstract fun SealDao(): SealDao
    abstract fun LogisticsLogDao(): LogisticsLogDao
    abstract fun MovementOrderDao(): MovementOrderDao
    abstract fun PathSegmentDao(): PathSegmentDao
    abstract fun SupplyDepotDao(): SupplyDepotDao
    abstract fun SupplyLineDao(): SupplyLineDao
    abstract fun CourtDao(): CourtDao
    abstract fun CourtExpenseDao(): CourtExpenseDao
    abstract fun FamilyDao(): FamilyDao
    abstract fun FavoriteDao(): FavoriteDao
    abstract fun NobleDao(): NobleDao
    abstract fun NobleTitleDao(): NobleTitleDao
    abstract fun PositionDao(): PositionDao
    abstract fun PrestigeDao(): PrestigeDao
    abstract fun RespectDao(): RespectDao
    abstract fun TitleDao(): TitleDao
    abstract fun TraitDao(): TraitDao
    abstract fun ScenarioGoalDao(): ScenarioGoalDao
    abstract fun WorldCommitDao(): WorldCommitDao
    abstract fun AmbassadorDao(): AmbassadorDao
    abstract fun CasusBelliDao(): CasusBelliDao
    abstract fun CountryDao(): CountryDao
    abstract fun DiplomacyDao(): DiplomacyDao
    abstract fun MarriageDao(): MarriageDao
    abstract fun SuccessionDao(): SuccessionDao
    abstract fun HouseholdDao(): HouseholdDao
    abstract fun MayorDao(): MayorDao
    abstract fun MayorOrderDao(): MayorOrderDao
    abstract fun NationalityDao(): NationalityDao
    abstract fun PopulationDao(): PopulationDao
    abstract fun SatisfactionDao(): SatisfactionDao
    abstract fun WorkDao(): WorkDao
    abstract fun BanditGroupDao(): BanditGroupDao
    abstract fun OutlawDao(): OutlawDao
    abstract fun RebelArmyDao(): RebelArmyDao
    abstract fun RebellionDao(): RebellionDao
    abstract fun SuppressionLogDao(): SuppressionLogDao
    abstract fun CelibacyRuleDao(): CelibacyRuleDao
    abstract fun ConversionDao(): ConversionDao
    abstract fun LeaderDao(): LeaderDao
    abstract fun MonasteryDao(): MonasteryDao
    abstract fun MonkDao(): MonkDao
    abstract fun PriestDao(): PriestDao
    abstract fun RankDao(): RankDao
    abstract fun ReligionDao(): ReligionDao
    abstract fun ReligiousClashDao(): ReligiousClashDao
    abstract fun TempleDao(): TempleDao
    abstract fun ToleranceDao(): ToleranceDao
    abstract fun OfficeAssignmentDao(): OfficeAssignmentDao
    abstract fun OfficeDao(): OfficeDao
    abstract fun RoleAssignmentDao(): RoleAssignmentDao
    abstract fun RoleDao(): RoleDao
    abstract fun BuildOrderDao(): BuildOrderDao
    abstract fun CrossingDao(): CrossingDao
    abstract fun DamDao(): DamDao
    abstract fun FloodDao(): FloodDao
    abstract fun FortificationDao(): FortificationDao
    abstract fun LandDao(): LandDao
    abstract fun OwnershipDao(): OwnershipDao
    abstract fun RiverDao(): RiverDao
    abstract fun RiverSegmentDao(): RiverSegmentDao
    abstract fun SiegeworkDao(): SiegeworkDao
    abstract fun StructureDao(): StructureDao
    abstract fun StructureEffectDao(): StructureEffectDao
    abstract fun StructureProgressDao(): StructureProgressDao
    abstract fun TerrainDao(): TerrainDao
    abstract fun WaterPoisonDao(): WaterPoisonDao
}