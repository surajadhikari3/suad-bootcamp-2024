package io.reactivestax.repository.hibernate;

import io.reactivestax.types.contract.repository.PayloadRepository;
import io.reactivestax.types.dto.Trade;
import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.types.enums.LookUpStatusEnum;
import io.reactivestax.types.enums.PostedStatusEnum;
import io.reactivestax.types.enums.StatusReasonEnum;
import io.reactivestax.types.enums.ValidityStatusEnum;
import io.reactivestax.utility.database.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.Optional;

import static io.reactivestax.utility.Utility.checkValidity;
import static io.reactivestax.utility.Utility.prepareTrade;


public class HibernateTradePayloadRepository implements PayloadRepository {

    private static HibernateTradePayloadRepository instance;

    private HibernateTradePayloadRepository() {
    }

    public static synchronized HibernateTradePayloadRepository getInstance() {
        if (instance == null) {
            instance = new HibernateTradePayloadRepository();
        }
        return instance;
    }

    @Override
    public Optional<String> insertTradeIntoTradePayloadTable(String payload) {
        Session session = HibernateUtil.getInstance().getConnection();
        Trade trade = prepareTrade(payload);
        TradePayload tradePayload = TradePayload.builder()
                .tradeId(trade.getTradeIdentifier())
                .validityStatus(checkValidity(payload.split(",")) ? ValidityStatusEnum.VALID : ValidityStatusEnum.INVALID)
                .statusReason(checkValidity(payload.split(",")) ?StatusReasonEnum.ALL_FIELDS_PRESENT : StatusReasonEnum.FIELDS_MISSING)
                .lookupStatus(LookUpStatusEnum.FAIL)
                .jeStatus(PostedStatusEnum.NOT_POSTED)
                .payload(payload)
                .build();

        session.persist(tradePayload);
        return Optional.ofNullable(tradePayload.getTradeId());
    }


    @Override
    public void updateLookUpStatus(String tradeNumber) {
        Session session = HibernateUtil.getInstance().getConnection();
        session.beginTransaction();
        Optional<TradePayload> optionalTradePayload = session.createQuery("FROM TradePayload WHERE tradeId = :tradeId", TradePayload.class)
                .setParameter("tradeId", tradeNumber)
                .stream()
                .findFirst();

        optionalTradePayload.ifPresent(tradePayload -> {
            tradePayload.setLookupStatus(LookUpStatusEnum.PASS);
            session.persist(tradePayload);
        });

        session.getTransaction().commit();
    }


    @Override
    public void updateJournalStatus(String tradeId) {
        Session session = HibernateUtil.getInstance().getConnection();
        session.beginTransaction();
        Optional<TradePayload> optionalTradePayload = session.createQuery("FROM TradePayload WHERE tradeId = :tradeId", TradePayload.class)
                .setParameter("tradeId", tradeId)
                .stream()
                .findFirst();
        optionalTradePayload.ifPresent(tradePayload -> {
            tradePayload.setJeStatus(PostedStatusEnum.POSTED);
            session.persist(tradePayload);
        });
        session.getTransaction().commit();
    }

    //using the criteria api for returning the payloadByTradeId
    @Override
    public Optional<String> readTradePayloadByTradeId(String tradeNumber) {
        Session session = HibernateUtil.getInstance().getConnection();
        final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<String> query = criteriaBuilder.createQuery(String.class);
        Root<TradePayload> root = query.from(TradePayload.class);
        query.select(root.get("payload"));
        query.where(criteriaBuilder.equal(root.get("tradeId"), tradeNumber));

        // Limit the result to only 1 record
        return Optional.ofNullable(session.createQuery(query)
                .setMaxResults(1)
                .getSingleResult()
        );
    }

}