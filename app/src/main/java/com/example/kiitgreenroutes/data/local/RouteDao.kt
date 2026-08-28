package com.example.kiitgreenroutes.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<RouteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<StopEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteStops(crossRefs: List<RouteStopCrossRef>)

    @Transaction
    @Query("SELECT * FROM routes WHERE id = :routeId")
    fun getRouteWithStops(routeId: String): Flow<RouteWithStops>
}

data class RouteWithStops(
    @Embedded val route: RouteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(RouteStopCrossRef::class, parentColumn = "routeId", entityColumn = "stopId")
    )
    val stops: List<StopEntity>
)
