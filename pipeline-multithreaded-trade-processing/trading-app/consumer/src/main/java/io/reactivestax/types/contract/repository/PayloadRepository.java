package io.reactivestax.types.contract.repository;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Optional;

public interface PayloadRepository {
    void updateLookUpStatus(String tradeId) throws SQLException, FileNotFoundException;

    void insertTradeIntoTradePayloadTable(String payload) throws Exception;

    void updateJournalStatus(String tradeId) throws SQLException, FileNotFoundException;

     Optional<String> readTradePayloadByTradeId(String tradeId) throws FileNotFoundException, SQLException;
}