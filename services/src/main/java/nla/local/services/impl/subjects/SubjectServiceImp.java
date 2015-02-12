package nla.local.services.impl.subjects;

import nla.local.dao.BaseDao;
import nla.local.dao.exceptions.DaoException;
import nla.local.pojos.Person;
import nla.local.services.ISubjectService;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Created by beresnev on 23.01.2015.
 */
@Service
@Transactional
public class SubjectServiceImp<T extends Person> extends BaseDao<T> implements ISubjectService<T> {

    private static Logger log = Logger.getLogger(SubjectServiceImp.class);

    private DetachedCriteria query;


    public DetachedCriteria getQuery()
    {
        return query;
    }

    public void setQuery(DetachedCriteria query)
    {
        this.query = query;
    }


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


}
