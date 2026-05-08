package com.example.mygame.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mygame.database.AppDatabase
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
import com.example.mygame.engine_and_helpers.roles_and_offices.AppDbCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

//    @Provides @Singleton
//    fun provideDbCallback(cb: AppDbCallback): RoomDatabase.Callback = cb

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context,
//                  cb: RoomDatabase.Callback
    ): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "mygame.db")
            .fallbackToDestructiveMigration() // dev only
//            .addCallback(cb)
            .build()

    // ==== ONE provider function per DAO you inject ====
    @Provides fun providePlannedBallAlcoholDao(db: AppDatabase): PlannedBallAlcoholDao = db.PlannedBallAlcoholDao()
    @Provides fun providePlannedBallBardDao(db: AppDatabase): PlannedBallBardDao = db.PlannedBallBardDao()
    @Provides fun providePlannedBallGuestDao(db: AppDatabase): PlannedBallGuestDao = db.PlannedBallGuestDao()
    @Provides fun providePlannedBallDao(db: AppDatabase): PlannedBallDao = db.PlannedBallDao()
    @Provides fun provideCourtMembershipDao(db: AppDatabase): CourtMembershipDao = db.CourtMembershipDao()
    @Provides fun provideUnitCompositionDao(db: AppDatabase): UnitCompositionDao = db.UnitCompositionDao()
    @Provides fun provideBattleTurnDao(db: AppDatabase): BattleTurnDao = db.BattleTurnDao()
    @Provides fun provideBudgetRequestDao(db: AppDatabase): BudgetRequestDao = db.BudgetRequestDao()
    @Provides fun provideSpreadEventDao(db: AppDatabase): SpreadEventDao = db.SpreadEventDao()
    @Provides fun provideRevoltDao(db: AppDatabase): RevoltDao = db.RevoltDao()
    @Provides fun provideGossipDao(db: AppDatabase): GossipDao = db.GossipDao()
    @Provides fun providePoisonAttemptDao(db: AppDatabase): PoisonAttemptDao = db.PoisonAttemptDao()
    @Provides fun provideHuntEventDao(db: AppDatabase): HuntEventDao = db.HuntEventDao()
    @Provides fun provideBallDao(db: AppDatabase): BallDao = db.BallDao()
    @Provides fun provideFestivalEventDao(db: AppDatabase): FestivalEventDao = db.FestivalEventDao()
    @Provides fun provideRouteStepDao(db: AppDatabase): RouteStepDao = db.RouteStepDao()
    @Provides fun provideRouteDao(db: AppDatabase): RouteDao = db.RouteDao()
    @Provides fun provideSecretRouteDao(db: AppDatabase): SecretRouteDao = db.SecretRouteDao()
    @Provides fun provideRequisitionDao(db: AppDatabase): RequisitionDao = db.RequisitionDao()
    @Provides fun provideMoraleHitDao(db: AppDatabase): MoraleHitDao = db.MoraleHitDao()
    @Provides fun provideFatigueStateDao(db: AppDatabase): FatigueStateDao = db.FatigueStateDao()
    @Provides fun provideRationPlanDao(db: AppDatabase): RationPlanDao = db.RationPlanDao()
    @Provides fun provideWagonDao(db: AppDatabase): WagonDao = db.WagonDao()
    @Provides fun provideConvoyDao(db: AppDatabase): ConvoyDao = db.ConvoyDao()
    @Provides fun provideShipDao(db: AppDatabase): ShipDao = db.ShipDao()
    @Provides fun provideBoatDao(db: AppDatabase): BoatDao = db.BoatDao()
    @Provides fun provideHazardDao(db: AppDatabase): HazardDao = db.HazardDao()
    @Provides fun provideWaterSourceDao(db: AppDatabase): WaterSourceDao = db.WaterSourceDao()
    @Provides fun provideCourtPositionDao(db: AppDatabase): CourtPositionDao = db.CourtPositionDao()
    @Provides fun provideTitleTrackDao(db: AppDatabase): TitleTrackDao = db.TitleTrackDao()
    @Provides fun provideChancellorDao(db: AppDatabase): ChancellorDao = db.ChancellorDao()
    @Provides fun provideGuildDao(db: AppDatabase): GuildDao = db.GuildDao()
    @Provides fun provideOppressionDao(db: AppDatabase): OppressionDao = db.OppressionDao()
    @Provides fun provideAssassinationDao(db: AppDatabase): AssassinationDao = db.AssassinationDao()
    @Provides fun provideFlagDao(db: AppDatabase): FlagDao = db.FlagDao()
    @Provides fun provideDuelEventDao(db: AppDatabase): DuelEventDao = db.DuelEventDao()
    @Provides fun provideDuelBanDao(db: AppDatabase): DuelBanDao = db.DuelBanDao()
    @Provides fun providePersonalConflictDao(db: AppDatabase): PersonalConflictDao = db.PersonalConflictDao()
    @Provides fun provideOffenseDao(db: AppDatabase): OffenseDao = db.OffenseDao()
    @Provides fun provideEvidenceDao(db: AppDatabase): EvidenceDao = db.EvidenceDao()
    @Provides fun provideCaseDao(db: AppDatabase): CaseDao = db.CaseDao()
    @Provides fun provideBanditArmyDao(db: AppDatabase): BanditArmyDao = db.BanditArmyDao()
    @Provides fun provideDesertionDao(db: AppDatabase): DesertionDao = db.DesertionDao()
    @Provides fun provideTacticDao(db: AppDatabase): TacticDao = db.TacticDao()
    @Provides fun provideLootDao(db: AppDatabase): LootDao = db.LootDao()
    @Provides fun provideSignalChannelDao(db: AppDatabase): SignalChannelDao = db.SignalChannelDao()
    @Provides fun provideEquipmentStockDao(db: AppDatabase): EquipmentStockDao = db.EquipmentStockDao()
    @Provides fun provideMoraleDao(db: AppDatabase): MoraleDao = db.MoraleDao()
    @Provides fun provideEmbargoDao(db: AppDatabase): EmbargoDao = db.EmbargoDao()
    @Provides fun provideManifestDao(db: AppDatabase): ManifestDao = db.ManifestDao()
    @Provides fun provideLossTimerDao(db: AppDatabase): LossTimerDao = db.LossTimerDao()
    @Provides fun provideTaxCollectionDao(db: AppDatabase): TaxCollectionDao = db.TaxCollectionDao()
    @Provides fun provideTaxPolicyDao(db: AppDatabase): TaxPolicyDao = db.TaxPolicyDao()
    @Provides fun provideMineDao(db: AppDatabase): MineDao = db.MineDao()
    @Provides fun provideFisheryDao(db: AppDatabase): FisheryDao = db.FisheryDao()
    @Provides fun provideSawmillDao(db: AppDatabase): SawmillDao = db.SawmillDao()
    @Provides fun provideFarmDao(db: AppDatabase): FarmDao = db.FarmDao()
    @Provides fun provideWarehouseDao(db: AppDatabase): WarehouseDao = db.WarehouseDao()
    @Provides fun provideStockDao(db: AppDatabase): StockDao = db.StockDao()
    @Provides fun provideCraftOrderDao(db: AppDatabase): CraftOrderDao = db.CraftOrderDao()
    @Provides fun provideMintOrderDao(db: AppDatabase): MintOrderDao = db.MintOrderDao()
    @Provides fun provideRecipeDao(db: AppDatabase): RecipeDao = db.RecipeDao()
    @Provides fun provideNeighborDao(db: AppDatabase): NeighborDao = db.NeighborDao()
    @Provides fun provideAmmoStockDao(db: AppDatabase): AmmoStockDao = db.AmmoStockDao()
    @Provides fun provideTreasuryDao(db: AppDatabase): TreasuryDao = db.TreasuryDao()
    @Provides fun provideMarketStockDao(db: AppDatabase): MarketStockDao = db.MarketStockDao()
    @Provides fun providePriceDao(db: AppDatabase): PriceDao = db.PriceDao()
    @Provides fun provideScenarioDao(db: AppDatabase): ScenarioDao = db.scenarioDao()
    @Provides fun provideWorldCommitDao(db: AppDatabase): WorldCommitDao = db.WorldCommitDao()
    @Provides fun provideTurnClockDao(db: AppDatabase): TurnClockDao = db.TurnClockDao()
    @Provides fun provideAuditLogDao(db: AppDatabase): AuditLogDao = db.AuditLogDao()
    @Provides fun provideSessionDAO(db: AppDatabase): SessionDAO = db.sessionDAO()
    @Provides fun provideActorDAO(db: AppDatabase): ActorDAO = db.actorDAO()
    @Provides fun provideActorLocationDAO(db: AppDatabase): ActorLocationDAO = db.actorLocationDAO()
    @Provides fun provideArmyDAO(db: AppDatabase): ArmyDao = db.ArmyDao()
    @Provides fun provideBattleDao(db: AppDatabase): BattleDao = db.BattleDao()
    @Provides fun provideBattleParticipantDao(db: AppDatabase): BattleParticipantDao = db.BattleParticipantDao()
    @Provides fun provideMilitaryOrderDao(db: AppDatabase): MilitaryOrderDao = db.MilitaryOrderDao()
    @Provides fun provideSiegeDao(db: AppDatabase): SiegeDao = db.SiegeDao()
    @Provides fun provideUnitDao(db: AppDatabase): UnitDao = db.UnitDao()
    @Provides fun provideConflictDao(db: AppDatabase): ConflictDao = db.ConflictDao()
    @Provides fun provideDuelDao(db: AppDatabase): DuelDao = db.DuelDao()
    @Provides fun provideHonorLogDao(db: AppDatabase): HonorLogDao = db.HonorLogDao()
    @Provides fun provideMarketDao(db: AppDatabase): MarketDao = db.MarketDao()
    @Provides fun provideResourceProductionDao(db: AppDatabase): ResourceProductionDao = db.ResourceProductionDao()
    @Provides fun provideResourceStockDao(db: AppDatabase): ResourceStockDao = db.ResourceStockDao()
    @Provides fun provideTradeAgreementDao(db: AppDatabase): TradeAgreementDao = db.TradeAgreementDao()
    @Provides fun provideTradeRouteDao(db: AppDatabase): TradeRouteDao = db.TradeRouteDao()
    @Provides fun provideFestivalDao(db: AppDatabase): FestivalDao = db.FestivalDao()
    @Provides fun providePerformanceDao(db: AppDatabase): PerformanceDao = db.PerformanceDao()
    @Provides fun provideTournamentDao(db: AppDatabase): TournamentDao = db.TournamentDao()
    @Provides fun provideIntegrityDao(db: AppDatabase): IntegrityDao = db.IntegrityDao()
    @Provides fun provideRuleOverrideDao(db: AppDatabase): RuleOverrideDao = db.RuleOverrideDao()
    @Provides fun provideCrimeDao(db: AppDatabase): CrimeDao = db.CrimeDao()
    @Provides fun providePunishmentDao(db: AppDatabase): PunishmentDao = db.PunishmentDao()
    @Provides fun provideTrialDao(db: AppDatabase): TrialDao = db.TrialDao()
    @Provides fun provideVerdictDao(db: AppDatabase): VerdictDao = db.VerdictDao()
    @Provides fun provideDoveDao(db: AppDatabase): DoveDao = db.DoveDao()
    @Provides fun provideInterceptionDao(db: AppDatabase): InterceptionDao = db.InterceptionDao()
    @Provides fun provideKnowledgeDao(db: AppDatabase): KnowledgeDao = db.KnowledgeDao()
    @Provides fun provideMessageDao(db: AppDatabase): MessageDao = db.MessageDao()
    @Provides fun provideMessengerDao(db: AppDatabase): MessengerDao = db.MessengerDao()
    @Provides fun providePostOfficeDao(db: AppDatabase): PostOfficeDao = db.PostOfficeDao()
    @Provides fun provideSealDao(db: AppDatabase): SealDao = db.SealDao()
    @Provides fun provideLogisticsLogDao(db: AppDatabase): LogisticsLogDao = db.LogisticsLogDao()
    @Provides fun provideMovementOrderDao(db: AppDatabase): MovementOrderDao = db.MovementOrderDao()
    @Provides fun providePathSegmentDao(db: AppDatabase): PathSegmentDao = db.PathSegmentDao()
    @Provides fun provideSupplyDepotDao(db: AppDatabase): SupplyDepotDao = db.SupplyDepotDao()
    @Provides fun provideSupplyLineDao(db: AppDatabase): SupplyLineDao = db.SupplyLineDao()
    @Provides fun provideCourtDao(db: AppDatabase): CourtDao = db.CourtDao()
    @Provides fun provideCourtExpenseDao(db: AppDatabase): CourtExpenseDao = db.CourtExpenseDao()
    @Provides fun provideFamilyDao(db: AppDatabase): FamilyDao = db.FamilyDao()
    @Provides fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.FavoriteDao()
    @Provides fun provideNobleDao(db: AppDatabase): NobleDao = db.NobleDao()
    @Provides fun provideNobleTitleDao(db: AppDatabase): NobleTitleDao = db.NobleTitleDao()
    @Provides fun providePositionDao(db: AppDatabase): PositionDao = db.PositionDao()
    @Provides fun providePrestigeDao(db: AppDatabase): PrestigeDao = db.PrestigeDao()
    @Provides fun provideRespectDao(db: AppDatabase): RespectDao = db.RespectDao()
    @Provides fun provideTitleDao(db: AppDatabase): TitleDao = db.TitleDao()
    @Provides fun provideTraitDao(db: AppDatabase): TraitDao = db.TraitDao()
    @Provides fun provideScenarioGoalDao(db: AppDatabase): ScenarioGoalDao = db.ScenarioGoalDao()
    @Provides fun provideAmbassadorDao(db: AppDatabase): AmbassadorDao = db.AmbassadorDao()
    @Provides fun provideCasusBelliDao(db: AppDatabase): CasusBelliDao = db.CasusBelliDao()
    @Provides fun provideCountryDao(db: AppDatabase): CountryDao = db.CountryDao()
    @Provides fun provideDiplomacyDao(db: AppDatabase): DiplomacyDao = db.DiplomacyDao()
    @Provides fun provideMarriageDao(db: AppDatabase): MarriageDao = db.MarriageDao()
    @Provides fun provideSuccessionDao(db: AppDatabase): SuccessionDao = db.SuccessionDao()
    @Provides fun provideHouseholdDao(db: AppDatabase): HouseholdDao = db.HouseholdDao()
    @Provides fun provideMayorDao(db: AppDatabase): MayorDao = db.MayorDao()
    @Provides fun provideMayorOrderDao(db: AppDatabase): MayorOrderDao = db.MayorOrderDao()
    @Provides fun provideNationalityDao(db: AppDatabase): NationalityDao = db.NationalityDao()
    @Provides fun providePopulationDao(db: AppDatabase): PopulationDao = db.PopulationDao()
    @Provides fun provideSatisfactionDao(db: AppDatabase): SatisfactionDao = db.SatisfactionDao()
    @Provides fun provideWorkDao(db: AppDatabase): WorkDao = db.WorkDao()
    @Provides fun provideBanditGroupDao(db: AppDatabase): BanditGroupDao = db.BanditGroupDao()
    @Provides fun provideOutlawDao(db: AppDatabase): OutlawDao = db.OutlawDao()
    @Provides fun provideRebelArmyDao(db: AppDatabase): RebelArmyDao = db.RebelArmyDao()
    @Provides fun provideRebellionDao(db: AppDatabase): RebellionDao = db.RebellionDao()
    @Provides fun provideSuppressionLogDao(db: AppDatabase): SuppressionLogDao = db.SuppressionLogDao()
    @Provides fun provideCelibacyRuleDao(db: AppDatabase): CelibacyRuleDao = db.CelibacyRuleDao()
    @Provides fun provideConversionDao(db: AppDatabase): ConversionDao = db.ConversionDao()
    @Provides fun provideLeaderDao(db: AppDatabase): LeaderDao = db.LeaderDao()
    @Provides fun provideMonasteryDao(db: AppDatabase): MonasteryDao = db.MonasteryDao()
    @Provides fun provideMonkDao(db: AppDatabase): MonkDao = db.MonkDao()
    @Provides fun providePriestDao(db: AppDatabase): PriestDao = db.PriestDao()
    @Provides fun provideRankDao(db: AppDatabase): RankDao = db.RankDao()
    @Provides fun provideReligionDao(db: AppDatabase): ReligionDao = db.ReligionDao()
    @Provides fun provideReligiousClashDao(db: AppDatabase): ReligiousClashDao = db.ReligiousClashDao()
    @Provides fun provideTempleDao(db: AppDatabase): TempleDao = db.TempleDao()
    @Provides fun provideToleranceDao(db: AppDatabase): ToleranceDao = db.ToleranceDao()
    @Provides fun provideOfficeAssignmentDao(db: AppDatabase): OfficeAssignmentDao = db.OfficeAssignmentDao()
    @Provides fun provideOfficeDao(db: AppDatabase): OfficeDao = db.OfficeDao()
    @Provides fun provideRoleAssignmentDao(db: AppDatabase): RoleAssignmentDao = db.RoleAssignmentDao()
    @Provides fun provideRoleDao(db: AppDatabase): RoleDao = db.RoleDao()
    @Provides fun provideBuildOrderDao(db: AppDatabase): BuildOrderDao = db.BuildOrderDao()
    @Provides fun provideCrossingDao(db: AppDatabase): CrossingDao = db.CrossingDao()
    @Provides fun provideDamDao(db: AppDatabase): DamDao = db.DamDao()
    @Provides fun provideFloodDao(db: AppDatabase): FloodDao = db.FloodDao()
    @Provides fun provideFortificationDao(db: AppDatabase): FortificationDao = db.FortificationDao()
    @Provides fun provideLandDao(db: AppDatabase): LandDao = db.LandDao()
    @Provides fun provideOwnershipDao(db: AppDatabase): OwnershipDao = db.OwnershipDao()
    @Provides fun provideRiverDao(db: AppDatabase): RiverDao = db.RiverDao()
    @Provides fun provideRiverSegmentDao(db: AppDatabase): RiverSegmentDao = db.RiverSegmentDao()
    @Provides fun provideSiegeworkDao(db: AppDatabase): SiegeworkDao = db.SiegeworkDao()
    @Provides fun provideStructureDao(db: AppDatabase): StructureDao = db.StructureDao()
    @Provides fun provideStructureEffectDao(db: AppDatabase): StructureEffectDao = db.StructureEffectDao()
    @Provides fun provideStructureProgressDao(db: AppDatabase): StructureProgressDao = db.StructureProgressDao()
    @Provides fun provideTerrainDao(db: AppDatabase): TerrainDao = db.TerrainDao()
    @Provides fun provideWaterPoisonDao(db: AppDatabase): WaterPoisonDao = db.WaterPoisonDao()
}