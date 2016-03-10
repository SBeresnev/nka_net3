package nla.local.services.impl;

import nla.local.exception.ServiceDaoException;
import nla.local.exception.ServiceException;
import nla.local.pojos.rights.Right;
import nla.local.pojos.rights.RightOwner;
import nla.local.services.IAddressService;
import nla.local.services.IObjectService;
import nla.local.services.IOperationsService;
import nla.local.services.IRightService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Created by beresnev on 23.06.2015.
 */
@Service
@Transactional
public class RightServiceImp extends BaseServiceImp implements IRightService {

    @Autowired
    private IObjectService ios;

    @Autowired
    private IOperationsService iopers;

    @Autowired
    private IAddressService ias;

    private static Logger log = Logger.getLogger(RightServiceImp.class);

    private DetachedCriteria query_Right = DetachedCriteria.forClass(Right.class,"rght").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

    private DetachedCriteria query_RightOwn = DetachedCriteria.forClass(RightOwner.class,"rght_own").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

    /******************************************Operation block**********************************************************************/


    public void rightOwnerbyDateFilter(List<Right> rights) throws ServiceDaoException {

        final Date curDate = new Date();

        for (Right right : rights) {

            CollectionUtils.filter(right.getRightOwners(), new Predicate() {
                public boolean evaluate(Object o) {
                    RightOwner ret_ = (RightOwner) o;
                    return ret_.getDate_out() != null ? ret_.getDate_out().after(curDate) : true;
                }
            });

        }

    }

    public void updateRight(Right right) throws ServiceDaoException {

        super.update(right);
    }

    @Override
    public void refreshRight(Right t) throws ServiceDaoException {

            super.refresh(t);

    };

    @Override
    public void updateRightOwner(RightOwner right_own) throws ServiceDaoException {

        super.update(right_own);

    }

    @Override
    public Right addRight(Right rght) throws ServiceDaoException, ServiceException {

        //////////////////////////// set parent operations by binding //////////////////////////////////////

        if(rght.getBindedObj().getOoper() != null) {  rght.getOoper().setParent_id_order(rght.getBindedObj().getOoper().getOoperId());}

        iopers.add(rght.getOoper());

        //////////////////////////// add operation ////////////////////////////////////////////////////////

        for(RightOwner rght_own : rght.getRightOwners())
         {
                rght_own.getOoper().setParent_id_order(rght.getOoper().getOoperId());
         }

          super.add(rght);

        return rght;
    }

    @Override
    public RightOwner addRightOwner(RightOwner rght_own) throws ServiceDaoException, ServiceException {

        super.add(rght_own);

        return rght_own;

    }

    /*************************************finds***************************************************************************/
    @Override
    public Right getRight(Long id) throws ServiceDaoException  {

        return (Right) super.get(Right.class,id);

    }

    @Override
    public RightOwner getRightOwner(Long id) throws ServiceDaoException {

        return (RightOwner) super.get(RightOwner.class,id);

    }

    @Deprecated
    public List<RightOwner> findbyrightCountType( Integer countType) throws ServiceDaoException {

        List<RightOwner> ret_val = null;

        DetachedCriteria query_own_ = (DetachedCriteria) SerializationUtils.clone(query_RightOwn);

        if( countType != null )
        {

            query_own_ = query_own_.add(Restrictions.eq("status", 1));

            query_own_ = query_own_.add(Restrictions.isNull("date_out"));

            query_own_ = query_own_.createCriteria("right").add(Restrictions.eq("right_count_type", countType));

            ret_val =  super.getCriterion(query_own_);

        }

        return ret_val;
    }

    @Override
    public List<RightOwner> getRighOwnPerson(Integer person_id, Long right_id) throws ServiceDaoException {

        boolean lakmus = false;

        List<RightOwner> ret_val = new ArrayList<RightOwner>();

        DetachedCriteria query_ = (DetachedCriteria) SerializationUtils.clone(query_RightOwn);

        query_ = query_.add(Restrictions.eq("status", 1));

        if(person_id != null) {

            lakmus = true;

            query_.add(Restrictions.eq("right_id",right_id));

            query_.add(Restrictions.eq("owner.subjectId", person_id)).add(Restrictions.or(Restrictions.ge("date_out", new Date()), Restrictions.isNull("date_out")));

        }

        ret_val = lakmus ? super.getCriterion(query_): null;

        return ret_val;

    }

    @Override
    public List<RightOwner> getRightOwnersbyRight(Long[] right_ids) throws ServiceDaoException, ServiceException {

        List<RightOwner> ret_val_own = null;

        DetachedCriteria query_ = (DetachedCriteria) SerializationUtils.clone(query_RightOwn);

        query_ = query_.add(Restrictions.eq("status", 1));

        query_ = query_.add(Restrictions.isNull("date_out"));

        query_ = query_.createCriteria("right").add(Restrictions.in("right_id", right_ids));

        ret_val_own =  super.getCriterion(query_);

        return ret_val_own;
    }

    ////Здесь нужно использовать или для поиска прав /////////////////////////////////////////////////////////////////////////
    @Override
    public List<Right> getlimitationsObject (Long right_id, Long right_owner_id)  throws ServiceDaoException, ServiceException {

        List<Right> ret_val = null;

        DetachedCriteria query_ = (DetachedCriteria) SerializationUtils.clone(query_Right);

        query_ = query_.add(Restrictions.between("right_type", 200, 400)); //// ограничения, обреминения

        if( right_id != null) {

            query_ = query_.add(Restrictions.eq("limit_right", right_id));

        }

        if( right_owner_id != null) {

            query_ = query_.createCriteria("rightOwners").add(Restrictions.eq("parent_owner", right_owner_id));
        }

        ret_val = super.getCriterion(query_);

        return ret_val;

    }

    public List<Right> getRightbyObjectPerson(Long[] obj_ids, Integer person_id) throws ServiceDaoException {

        boolean lakmus = false;

        final Date curDate = new Date();

        List<Right> ret_val = new ArrayList<Right>();

        DetachedCriteria query_ = (DetachedCriteria) SerializationUtils.clone(query_Right);

        query_ = query_.add(Restrictions.or(Restrictions.ge("end_date", curDate), Restrictions.isNull("end_date")));

        if( obj_ids != null && obj_ids.length > 0 )
        {
            lakmus = true;

            query_ = query_.add(Restrictions.in("bindedObj.obj_id",obj_ids));

        }


        if (person_id != null) {

            lakmus = true;

            query_ = query_.createCriteria("rightOwners").add(Restrictions.eq("owner.subjectId", person_id)).add(Restrictions.or(Restrictions.ge("date_out", curDate), Restrictions.isNull("date_out")));

        }

        ret_val = lakmus ? super.getCriterion(query_): null;

        return ret_val;

    }

    @Override
    public List<Right> getRightbyRightOwner(Long[] right_own_ids) throws ServiceDaoException {

        List<Right> val_rgt = null;

        List<Right> ret_val_rgt = new ArrayList<Right>();

        DetachedCriteria query_ = (DetachedCriteria) SerializationUtils.clone(query_Right);

        query_ = query_.add(Restrictions.eq("status", 1));

        query_ = query_.add(Restrictions.isNull("end_date"));

        query_ = query_.createCriteria("rightOwners").add(Restrictions.in("right_owner_id", right_own_ids));

        query_ = query_.add(Restrictions.eq("status", 1));

        val_rgt =  super.getCriterion(query_);

        ////////// find needed elements ////////////////////////////////////////////////////////////////////////////////

         final List<Long> in_list = Arrays.asList(right_own_ids);

         for (Right right : val_rgt){

             List<RightOwner> righttorem = (List<RightOwner>) CollectionUtils.select(right.getRightOwners(), new Predicate() {
                 public boolean evaluate(Object o) {
                     RightOwner ret_ = (RightOwner) o;
                     return in_list.contains(ret_.getRight_owner_id());
                 }
             });

             right.setRightOwners(righttorem);

          }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return val_rgt;

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////// /////////////////////////////////////////////
    //    все проводки по правам в случае дробления прав предполагают, что можно захрдкодить и опреацию закрытия от кого это право (часть прав) передается      //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void passSingleRight(RightOwner rght_own) throws ServiceDaoException, ServiceException {

        this.addRightOwner(rght_own);

    }

    @Override
    public void passSharedRight(HashMap<RightOwner,RightOwner> right_own) throws ServiceDaoException, ServiceException {

        /***************************** update parent owner **********************************************************/

        Right main_right =  right_own.get(right_own.keySet().iterator().next()).getRight();

        this.addRight(main_right);

        for (RightOwner parent_owner : right_own.keySet() )  {

            RightOwner child_own = right_own.get(parent_owner);

            this.updateRightOwner(parent_owner);

            child_own.setParent_owner(parent_owner.getRight_owner_id());

            child_own.getOoper().setParent_id_hist(parent_owner.getOoper().getOoperId());

            child_own.setRight(main_right);

            this.addRightOwner(child_own);

        }

    }

    @Override
    public void splitSharedRight(List<RightOwner> child_owners, RightOwner parent_owner) throws ServiceDaoException, ServiceException {

        /***************************** update parent owner **********************************************************/

        this.updateRightOwner(parent_owner);

        /**************************************************************************************************************/

        for (RightOwner child_owner : child_owners )
        {
            child_owner.setParent_owner(parent_owner.getRight_owner_id());

            child_owner.getOoper().setParent_id_hist(parent_owner.getOoper().getOoperId());

            this.addRightOwner(child_owner);
        }


    }


}
