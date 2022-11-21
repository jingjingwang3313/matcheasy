package cn.matcheasy.framework.config.transactional;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

/**
 * @class MultiDataSourceTransactionFactory
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: 支持Service内多数据源切换的Factory
 */
public class MultiDataSourceTransactionFactory extends SpringManagedTransactionFactory {

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new MultiDataSourceTransaction(dataSource);
    }

}
