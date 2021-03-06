package com.walkfun.service.vproduct.impl;

import com.walkfun.common.exception.ServerRequestException;
import com.walkfun.db.vproduct.dao.def.VProductDAO;
import com.walkfun.entity.account.UserInfo;
import com.walkfun.entity.account.UserProp;
import com.walkfun.entity.vproduct.*;
import com.walkfun.service.BaseService;
import com.walkfun.service.account.def.AccountService;
import com.walkfun.service.backend.BackendJobCache;
import com.walkfun.service.vproduct.def.VProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: p
 * Date: 14-2-14
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
public class VProductServiceImpl extends BaseService implements VProductService {

    @Autowired
    private VProductDAO vProductDAO;

    @Autowired
    private AccountService accountService;

    @Override
    public List<VProduct> getVProductForRest(Date lastUpdateTime) {
        try {
            if (lastUpdateTime.before(BackendJobCache.productFirstTime)) {
                return BackendJobCache.allProducts;
            }
            if (lastUpdateTime.after(BackendJobCache.productLastTime)) {
                return new ArrayList<VProduct>();
            }
            return getVProduct(lastUpdateTime);
        } catch (Exception ex) {
            throw new ServerRequestException(ex.getMessage());
        }
    }

    @Override
    public List<VProduct> getVProduct(Date lastUpdateTime) {
        try {
            return vProductDAO.getVProductByDate(lastUpdateTime);
        } catch (Exception ex) {
            throw new ServerRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void createVProductHistory(VProductHistory vProductHistory) {
        try {
            vProductDAO.createVProductHistory(vProductHistory);
            UserInfo userInfo = accountService.getAccountInfoByID(vProductHistory.getUserId(), null);
            List<UserProp> userProps = accountService.getUserProps(vProductHistory.getUserId(), null);
            List<UserProp> updateProps = new ArrayList<UserProp>();
            VProduct vProduct = getVProductById(vProductHistory.getProductId());
            //update old props
            for (UserProp userProp : userProps) {
                if (userProp.getProductId().intValue() == vProductHistory.getProductId().intValue()) {
                    userProp.setOwnNumber(userProp.getOwnNumber() + vProductHistory.getNumbers());
                    updateProps.add(userProp);
                    double price = -(double) (vProductHistory.getNumbers() * vProduct.getVirtualPrice());
                    userInfo.setGoldCoin(plus(userInfo.getGoldCoin(), price));
                    accountService.createOrUpdateUserProp(updateProps);
                    accountService.updateAccountInfo(userInfo);
                    return;
                }
            }
            // add new props
            if (vProduct != null) {
                UserProp newUserProp = new UserProp();
                newUserProp.setUserId(vProductHistory.getUserId());
                newUserProp.setProductId(vProductHistory.getProductId());
                newUserProp.setProductName(vProduct.getProductName());
                newUserProp.setOwnNumber(vProductHistory.getNumbers());
                updateProps.add(newUserProp);
                double price = -(double) (vProductHistory.getNumbers() * vProduct.getVirtualPrice());
                userInfo.setGoldCoin(plus(userInfo.getGoldCoin(), price));
                accountService.createOrUpdateUserProp(updateProps);
                accountService.updateAccountInfo(userInfo);
            }
        } catch (Exception ex) {
            throw new ServerRequestException(ex.getMessage());
        }
    }

    @Override
    public VProduct getVProductById(Integer vProductId) {
        for (VProduct vProduct : BackendJobCache.allProducts) {
            if (vProduct.getProductId().intValue() == vProductId.intValue()) {
                return vProduct;
            }
        }
        return null;
    }
}
