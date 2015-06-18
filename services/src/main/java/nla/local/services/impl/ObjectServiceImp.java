package nla.local.services.impl;

import nla.local.exception.ServiceDaoException;
import nla.local.exception.ServiceException;
import nla.local.pojos.address.Address_dest;
import nla.local.pojos.address.Address_src;
import nla.local.pojos.object.Object;
import nla.local.pojos.object.Object_dest;
import nla.local.pojos.object.Object_src;
import nla.local.services.IAddressService;
import nla.local.services.IObjectService;
import nla.local.util.CodeGenerator;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by beresnev on 03.06.2015.
 */
@Service
@Transactional
public class ObjectServiceImp extends BaseServiceImp implements IObjectService{

    private static Logger log = Logger.getLogger(ObjectServiceImp.class);

    @Autowired
    private IAddressService ias;

    @Autowired
    private CodeGenerator scg;


    public List<Object_dest> findbyobjDestid(Long obj_dest_id) throws ServiceDaoException
    {
        List<Object_dest> ret_val = null;

        DetachedCriteria query_ = DetachedCriteria.forClass(Object_dest.class);

        if( obj_dest_id != null) {

            query_ = query_.add(Restrictions.eq("obj_dest_id", obj_dest_id)) ;

            ret_val = super.getCriterion(query_);
        }

        return ret_val;


    }


    public Object_dest convertSrctoDest(Object_src adr_src) throws ServiceDaoException
    {

        Object_dest ret_val = new Object_dest();

        ret_val.setObj_id_inv(adr_src.getObj_id());

        ret_val.setCadaster_number(adr_src.getCadaster_number());

        ret_val.setOrg_id(adr_src.getOrg_id());

        ret_val.setObjectType(adr_src.getObjectType());

        ret_val.setInventory_number(adr_src.getInventory_number());

        ret_val.setSquare(adr_src.getSquare());

        ret_val.setRoomscount(adr_src.getRoomscount());

        ret_val.setReadiness(adr_src.getReadiness());

        ret_val.setObject_name(adr_src.getObject_name());

        ret_val.setUse_purpose(adr_src.getUse_purpose());

        ret_val.setLand_category(adr_src.getLand_category());

        ret_val.setAdr_num(adr_src.getAddress_id());

        return ret_val;
    }


    @Transactional( rollbackFor=Exception.class)
    public Object_dest bindObject( Object_dest object_dest) throws ServiceDaoException, ServiceException
    {

        if (object_dest.getAddress_id() == null && object_dest.getAdr_num() != null)
        {

            List<Address_src> ret_src_list = ias.getsrcbyID(null, object_dest.getAdr_num(), object_dest.getObjectType().getCode_id());

            if(ret_src_list.size() > 0 ) {

                Address_dest adr_dst_t = ias.convertSrctoDest(ret_src_list.get(0));

                adr_dst_t = ias.bindAddress(adr_dst_t);

                object_dest.setAddress_dest(adr_dst_t);

                object_dest.setAddress_id(adr_dst_t.getAddress_id());

            } else throw new ServiceException(new Exception(),"No address found by object");

        }


        if( object_dest.getObj_id() != null )
        {
            this.update(object_dest);

        }else {

            Long objid = Long.valueOf(scg.generate("SEQ_OBJECTS_ID.NEXTVAL").toString());

            object_dest.setObj_id(objid);

            this.add(object_dest);
        }

        return object_dest;

    }


    public List<Object_dest> findObjectbyAddressCommon(List<Long> address_id) throws ServiceDaoException {

        List<Object_dest> ret_val_dest = null;

        List<Object_src> ret_val_src = null;

        ret_val_dest = (List<Object_dest>) findObjectbyAddress(Object_dest.class, address_id);

        if(ret_val_dest.size() == 0) {

            ret_val_src = (List<Object_src>) findObjectbyAddress(Object_src.class, address_id);

            for (Object_src src : ret_val_src)
            {

                Object_dest dest = convertSrctoDest(src);

                ret_val_dest.add(dest);

            }
        }

        return ret_val_dest;
    }


    public List<? extends Object> findObjectbyAddress(Class<? extends Object> cobj, List<Long> address_id) throws ServiceDaoException
    {
        List<? extends Object> ret_val = null;

        DetachedCriteria query_ = DetachedCriteria.forClass(cobj);

        query_ = query_.add(Restrictions.in("address_id", address_id));

        ret_val = super.getCriterion(query_);

        return ret_val;

    }

    public List<? extends Object>  findObjectbyCadastreNum(Class<? extends Object> cobj, String cadastre_number) throws ServiceDaoException
    {
        List<? extends Object> ret_val = null;

        DetachedCriteria query_ = DetachedCriteria.forClass(cobj);

        if( cadastre_number != null) {

            query_ = query_.add(Restrictions.eq("cadastre_number", cadastre_number)) ;

            ret_val = super.getCriterion(query_);
        }

        return  ret_val;
    }

    public List<? extends Object>  findObjectbyInventoryNum(Class<? extends Object> cobj, Integer inventory_number, Integer object_type, Integer org_id) throws ServiceDaoException
    {
        List<? extends Object> ret_val = null;

        DetachedCriteria query_ = DetachedCriteria.forClass(cobj);

        if( inventory_number != null) {

            query_ = query_.add(Restrictions.eq("inventory_number", inventory_number));

            query_ = query_.add(Restrictions.eq("org_id", org_id));

            query_ =  query_.createCriteria("objectType").add(Restrictions.eq("code_id", object_type));

            ret_val = super.getCriterion(query_);
        }

        return  ret_val;

    }

}
