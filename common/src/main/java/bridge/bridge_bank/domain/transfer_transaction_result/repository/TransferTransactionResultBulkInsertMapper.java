package bridge.bridge_bank.domain.transfer_transaction_result.repository;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransferTransactionResultBulkInsertMapper {

    void bulkInsert(@Param("results") List<TransferTransactionResult> results);
}
