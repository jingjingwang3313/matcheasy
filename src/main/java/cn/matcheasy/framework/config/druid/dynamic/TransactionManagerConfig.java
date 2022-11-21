package cn.matcheasy.framework.config.druid.dynamic;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;

/**
 * @class: TransactionManagerConfig
 * @author: wangjing
 * @date: 2021/1/12/0012
 * @desc: atomikos事务管理器, 分布式事务使用JTA管理，不管有多少个数据源只要配置一个JtaTransactionManager
 */
@Configuration
public class TransactionManagerConfig
{

    public UserTransactionManager userTransactionManager()
    {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    public UserTransactionImp userTransactionImp() throws SystemException
    {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(30000);
        return userTransactionImp;
    }

    @Primary
    @Bean(name = "jtaTransactionManager")
    public JtaTransactionManager jtaTransactionManager() throws SystemException
    {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(userTransactionManager());
        jtaTransactionManager.setUserTransaction(userTransactionImp());
        jtaTransactionManager.setAllowCustomIsolationLevels(true);
        jtaTransactionManager.setDefaultTimeout(30000);
        return jtaTransactionManager;
    }

}
