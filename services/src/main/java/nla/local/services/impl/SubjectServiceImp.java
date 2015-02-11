package nla.local.services.impl;

import nla.local.dao.BaseDao;
import nla.local.dao.exceptions.DaoException;
import nla.local.pojos.JPerson;
import nla.local.pojos.OPerson;
import nla.local.pojos.PPerson;
import nla.local.pojos.Person;
import nla.local.services.ISubjectService;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by beresnev on 23.01.2015.
 */
@Service
@Transactional
public class SubjectServiceImp<T extends Person> extends BaseDao<T> implements ISubjectService<T> {

    private static Logger log = Logger.getLogger(SubjectServiceImp.class);

    private DetachedCriteria query;

    @Autowired
   public SubjectServiceImp(SessionFactory sessionFactory)
    {
        super(sessionFactory);

    }

    @Override
    public void addSubject(T t) throws DaoException
    {

         super.add(t);

    }

    @Override
    public T getSubject(Class<T> clazz, Serializable id) throws DaoException
    {

        return super.get(clazz,id);

    };

    @Override
    public void refreshSubject(T t) throws DaoException
    {
            super.update(t);

    };

    @Override
    public List<T> findSubject(DetachedCriteria dc)
    {
        List<T> out = null;

        if (dc == null) { dc = getQuery(); }
        else { setQuery(dc);}

        try {

            out = super.getCriterion(query);

        } catch (DaoException e) {
            e.printStackTrace();
        }

        return (List<T>) out;
    };


    public List<T> findByNameType(String fullName, String regNumber, Integer subjectType )
    {

        log.info("Get " + " by name. Invoked SubjectService.getByNameType" );

         Type t = getClass().getGenericSuperclass();

        List<T> retval = new ArrayList<T>();

        if(fullName != null || regNumber != null) {

            regNumber = regNumber == null ? "":regNumber;
            fullName = fullName==null? "":fullName;

            query = DetachedCriteria.forClass(JPerson.class)
                    .add(Restrictions.or(Restrictions.like("regNumber", "%" + regNumber + "%", MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("regNumber")))
                    .add(Restrictions.or(Restrictions.like("fullname", "%" + fullName + "%",MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("fullname")));

            query = subjectType != null ? query.createCriteria("subjectType").add(Restrictions.eq("code_id", subjectType)):query;


            retval = (List<T>) this.findSubject(query);

        }

        return retval;
    }


    public List<T> findOffUser(String surname, String firstname, String fathername, Integer user_num, String orgname, Integer subjectType )
    {

        log.info("Get " + " by name. Invoked SubjectService.findOffUser" );

        List<T> retval =  new ArrayList<T>();

        if(surname != null || orgname != null) {

            surname = surname == null ? "":surname;
            firstname = firstname == null? "":firstname;
            fathername = fathername == null ? "":fathername;
            orgname = orgname == null ? "":orgname;

             query = DetachedCriteria.forClass(OPerson.class)
                    .add(Restrictions.or(Restrictions.like("surname", surname, MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("surname")))
                    .add(Restrictions.or(Restrictions.like("firstname", firstname, MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("firstname")))
                    .add(Restrictions.or(Restrictions.like("fathername", fathername, MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("fathername")))
                    .add(Restrictions.or(Restrictions.like("orgname", orgname, MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("orgname")));

            query = user_num != null ? query.add(Restrictions.eq("user_num", user_num)): query;
            query = subjectType != null ? query.createCriteria("subjectType").add(Restrictions.eq("code_id", subjectType)):query;

            retval = (List<T>) this.findSubject(query);

        }

        return retval;

    }

    public List<T> findByFIOType(String surname, String firstname, String fathername, String personalNumber, Integer subjectType )
    {

        log.info("Get " + " by name. Invoked SubjectService.getByFIOType" );

        List<T> retval =  new ArrayList<T>();

        if(surname != null || personalNumber != null) {

            surname = surname == null ? "":surname;
            firstname = firstname==null? "":firstname;
            fathername = fathername == null ? "":fathername;
            personalNumber = personalNumber == null ? "":personalNumber;

            query = DetachedCriteria.forClass(PPerson.class)
                    .add(Restrictions.or(Restrictions.like("surname", "%" + surname + "%", MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("surname")))
                    .add(Restrictions.or(Restrictions.like("firstname", "%" + firstname + "%", MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("firstname")))
                    .add(Restrictions.or(Restrictions.like("fathername", "%" + fathername + "%", MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("fathername")))
                    .add(Restrictions.or(Restrictions.like("personalNumber", "%" + personalNumber + "%", MatchMode.ANYWHERE).ignoreCase(), Restrictions.isNull("fathername")))
                    ;

            query = subjectType != null ? query.createCriteria("subjectType").add(Restrictions.eq("code_id", subjectType)):query;


            retval = (List<T>) this.findSubject(query);
        }

        return retval;

    }

    public DetachedCriteria getQuery()
    {
        return query;
    }

    public void setQuery(DetachedCriteria query)
    {
        this.query = query;
    }


}
