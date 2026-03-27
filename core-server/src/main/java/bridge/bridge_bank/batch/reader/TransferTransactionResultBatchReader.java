package bridge.bridge_bank.batch.reader;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class TransferTransactionResultBatchReader {

    /*@Bean
    @StepScope
    public JpaPagingItemReader<TransferTransactionResult> transferTransactionResultReader(
            @Value("#{stepExecutionContext['startId']}") Long startId,
            @Value("#{stepExecutionContext['endId']}") Long endId,
            SqlSessionFactory sqlSessionFactory
    ){

    }*/
}
