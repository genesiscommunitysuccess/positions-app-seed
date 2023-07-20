package global.genesis

import global.genesis.commons.model.GenesisSet
import global.genesis.dictionary.GenesisDictionary
import global.genesis.message.core.event.EventReply
import global.genesis.testsupport.AbstractGenesisTestSupport
import global.genesis.testsupport.GenesisTestConfig
import global.genesis.position.message.event.PositionReport
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PositionReportWriteTest : AbstractGenesisTestSupport<GenesisSet>(
    GenesisTestConfig {
        addPackageName("global.genesis.eventhandler.pal")
        genesisHome = "/GenesisHome/"
        parser = { it }
        scriptFileName = "position-eventhandler.kts"
        initialDataFile = "data/REPORT_DATA.csv"
        addAuthCacheOverride("ENTITY_VISIBILITY")
    }
) {
    override fun systemDefinition(): Map<String, Any> = mapOf(
        "IS_SCRIPT" to "true"
    )

    override fun createDictionary(): GenesisDictionary = prodDictionary()

    @Test
    fun `test write event`(): Unit = runBlocking {
        sendEvent(PositionReport()).assertedCast<EventReply.EventAck>()
    }
}
