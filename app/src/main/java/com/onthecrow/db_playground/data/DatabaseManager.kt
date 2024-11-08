package com.onthecrow.db_playground.data

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.onthecrow.db_playground.ColorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList
import kotlin.math.roundToInt

class DatabaseManager(
    applicationContext: Context
) {

    private val db = Room.databaseBuilder(
        applicationContext,
        SampleDatabase::class.java, "database-name"
    ).build()
    private var rangeProvider: (() -> IntRange)? = null
    private val changes = LinkedList<suspend () -> Unit>()
    private val mutex = Mutex()


    init {
        MainScope().launch(Dispatchers.IO) {
            prepopulateDb()
            runDbModifications()
            runSyncJob()
        }
    }

    /**
     * Sets the range provider function for this object.
     *
     * The range provider is a function that returns an IntRange, representing the **indices of currently visible list items**.
     * This range is used to determine which rows in the database need to be modified to reflect changes on the screen in real-time.
     *
     * @param rangeProvider A function that returns an IntRange, representing the indices of currently visible list items.
     */
    fun setRangeProvider(rangeProvider: () -> IntRange) {
        this.rangeProvider = rangeProvider
    }

    /**
     * Retrieves all data from the sample table in the database.
     *
     * @return A list of sample objects representing all users in the database.
     */
    fun getData() = db.userDao().getAll()

    /**
     * Prepopulates the database with sample data if it's empty.
     *
     * This function checks if the database already contains data by querying for a single entity.
     * If no entity is found, it generates 1,000,000 sample entities with random timestamps and boolean values.
     * These entities are then inserted into the database using the UserDao.
     */
    private suspend fun prepopulateDb() {
        if (db.userDao().getOne() != null) return
        val currentDate = System.currentTimeMillis()
        val entities = Array(1_000_000) {
            SampleEntity(
                "Firstname",
                "Lastname",
                currentDate - (Math.random() * 1_000_000).toLong(),
                Math.random().roundToInt() == 1,
                text = "Some sample text",
                color = null,
            )
        }
        db.userDao().insertAll(*entities)
    }


    /**
     * This function launches a coroutine on the IO dispatcher that continuously polls for changes
     * stored in the `changes` list. When changes are found, they are executed sequentially within
     * a database transaction using `db.withTransaction`. A mutex is used to ensure exclusive access
     * to the `changes` list during processing.
     *
     * The loop continues indefinitely, processing changes with a delay of 100 milliseconds between
     * each iteration.
     *
     * @note This function runs indefinitely in the background and should be started only once.
     */
    private fun runSyncJob() {
        MainScope().launch(Dispatchers.IO) {
            while (true) {
                delay(100)
                mutex.withLock {
                    db.withTransaction {
                        changes.map { lambda ->
                            launch { lambda() }
                        }.joinAll()
                    }
                    changes.clear()
                }
            }
        }
    }


    /**
     * Starts a periodic execution of a given change function.
     *
     * This function launches multiple coroutines, each repeating the provided `change` function
     * at a specified interval. The changes are added to a queue (`changes`) and processed sequentially.
     *
     * @param times The number of coroutines to launch, each executing the change periodically.
     * @param delay The delay in milliseconds between each execution of the `change` function.
     * @param change A suspend function representing the change to be applied periodically.
     */
    private fun startPeriodicChange(times: Int, delay: Long, change: suspend () -> Unit) {
        repeat(times) {
            MainScope().launch(Dispatchers.Default) {
                while (true) {
                    mutex.withLock {
                        changes.add {
                            change()
                        }
                    }
                    delay(delay)
                }
            }
        }
    }

    /**
     * Initiates periodic database modifications.
     *
     * This function starts several coroutines that perform various operations on the database at regular intervals.
     * These operations are designed to simulate ongoing database activity for testing and demonstration purposes.
     *
     * The following modifications are performed:
     *
     * **Random Entity Modifications:**
     * 1. **Updating entity color:** Randomly selects an entity and changes its color.
     * 2. **Toggling 'isRead' status:** Randomly selects an entity and toggles its 'isRead' flag.
     * 3. **Deleting an entity:** Randomly selects and deletes an entity.
     * 4. **Inserting a new entity:** Inserts a new sample entity with random data.
     *
     * Those modifications are intended to mock an intensive background activity (40 modifications per 100ms)
     *
     * **Modifications within a Range:**
     *  These modifications target entities within a specific range, potentially provided by `rangeProvider`.
     * 5. **Updating entity color:** Updates the color of an entity within the range.
     * 6. **Toggling 'isRead' status:** Toggles the 'isRead' status of an entity within the range.
     * 7. **Deleting an entity:** Deletes an entity within the range.
     *
     * Those modifications are intended to show that everything works smooth via rial-time changes on currently displayed elements
     *
     * The frequency and initial delay of these operations are defined by the parameters passed to the `startPeriodicChange` function.
     *  - The first parameter is the delay in milliseconds before the first execution.
     *  - The second parameter is the repeat interval in milliseconds.
     */
    private fun runDbModifications() {

        startPeriodicChange(10, 100) {
            val entity = db.userDao().getOne((1_000_000 * Math.random()).roundToInt() - 1)
            entity?.run { db.userDao().update(copy(color = ColorUtils.getRandomColor())) }
        }
        startPeriodicChange(10, 100) {
            val entity = db.userDao().getOne((1_000_000 * Math.random()).roundToInt() - 1)
            entity?.run { db.userDao().update(copy(isRead = this.isRead.not())) }
        }
        startPeriodicChange(10, 100) {
            val entity =
                db.userDao().getOne((1_000_000 * Math.random()).roundToInt() - 1)
            db.userDao().delete(entity!!)
        }
        startPeriodicChange(10, 100) {
            db.userDao().insertAll(
                SampleEntity(
                    "Firstname",
                    "Lastname",
                    System.currentTimeMillis() - (Math.random() * 1_000_000).toLong(),
                    Math.random().roundToInt() == 1,
                    text = "Some sample text",
                    color = null,
                )
            )
        }
        startPeriodicChange(1, 100) {
            with(rangeProvider?.invoke()) {
                if (this == null) return@with
                val index = (start..endInclusive).random()
                db.userDao().getOne(index)?.let { entity ->
                    db.userDao()
                        .update(entity.copy(color = ColorUtils.getRandomColor()))
                }
            }
        }
        startPeriodicChange(1, 100) {
            with(rangeProvider?.invoke()) {
                if (this == null) return@with
                val index = (start..endInclusive).random()
                db.userDao().getOne(index)?.let { entity ->
                    db.userDao().update(entity.copy(isRead = entity.isRead.not()))
                }
            }
        }
        startPeriodicChange(1, 100) {
            with(rangeProvider?.invoke()) {
                if (this == null) return@with
                val index = (start..endInclusive).random()
                db.userDao().getOne(index)?.let { entity ->
                    db.userDao().delete(entity)
                }
            }
        }
    }
}