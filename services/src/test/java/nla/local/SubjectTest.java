package nla.local;

import nla.local.dao.exceptions.DaoException;
import nla.local.pojos.dict.Dict;
import nla.local.pojos.dict.DictPk;
import nla.local.pojos.dict.EnumDict;
import nla.local.pojos.subjects.JPerson;
import nla.local.pojos.subjects.OPerson;
import nla.local.pojos.subjects.PPerson;
import nla.local.pojos.subjects.Person;
import nla.local.services.impl.DictionaryServiceImp;
import nla.local.services.impl.subjects.JSubjectServiceImp;
import nla.local.services.impl.subjects.OSubjectServiceImp;
import nla.local.services.impl.subjects.PSubjectServiceImp;
import nla.local.util.CodeGenerator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:beans-services.xml","classpath:beans-dao.xml"})
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class SubjectTest
{

    private static Logger log = Logger.getLogger(SubjectTest.class);


    @Autowired
    public OSubjectServiceImp oService;

    @Autowired
    public PSubjectServiceImp pService;

    @Autowired
    public JSubjectServiceImp jService;

    @Autowired
    public DictionaryServiceImp CommonDict;

    @Autowired
    public CodeGenerator scg;

    private List<Dict> subjectServDictList;
    private List<Dict> orgStructDictList;
    private List<Dict> stateDictList;
    private List<Dict> torStructDictList;

    private List<Dict> allDictList;

    @Before
    public void setUp() throws Exception {

        subjectServDictList = CommonDict.getDict(EnumDict.SubjectType);

        orgStructDictList = CommonDict.getDict(EnumDict.OrgStruct);

        stateDictList = CommonDict.getDict(EnumDict.State);

        torStructDictList = CommonDict.getDict(EnumDict.TorStruct);

        allDictList = CommonDict.getAll();

        DictPk pk = new DictPk(120,110);

        Dict dd = CommonDict.getDict(pk);

        Integer u =5;
    }

   // @org.junit.Test
    public void SubjectOffTestController()  {

        AddOffSubject();
        GetOffSubject();
        UpdateOffSubject();
    }

    @org.junit.Test
    public void SubjectJurTestController() {

       // AddJurSubject();
        GetJurSubject();
       // UpdateJurSubject();
    }

   //@org.junit.Test
    public void SubjectPhyTestController(){

        AddPhysSubject();
       // GetPhysSubject();
        //UpdatePhysSubject();

    }


    public void AddOffSubject() {
        log.info("Invoked SubjectTest.AddOffSubject()" );

        boolean retval = true;

        try {

            for (int i = 0; i < 10; i++) {

                OPerson op = new OPerson();
                op.subjectType = subjectServDictList.get(24);
                op.isOwner = 0;

                op.firstname = "Дударык" + String.valueOf(i);
                op.surname = "Труба" + String.valueOf(i);
                op.fathername = "Флейтович" + String.valueOf(i);

                op.org_kod =  torStructDictList.get(0);
                op.orgname = "РУП \"Брестское агентство по государственной регистрации и земельному кадастру\"";
                oService.addSubject(op);

            }
        }
            catch (DaoException e) {
                retval = false;
                e.printStackTrace();
            }

        assertTrue(retval);

    }

    public void GetOffSubject() {
        log.info("Invoked SubjectTest.GetOffSubject" );

        List<OPerson> result_o= oService.findOffUser("Тру", "", "", null, "Брест",null);

        assertTrue(!result_o.isEmpty());
    }

    public void UpdateOffSubject()  {

        log.info("Invoked SubjectTest.UpdateJurSubject" );

        boolean retval = true;

        try {

            for(int i=0; i<10; i++) {

                OPerson op = new OPerson();
                op.subjectType = subjectServDictList.get(24);
                op.isOwner = 0;

                op.surname = "Иванов" + String.valueOf(20+i);
                op.firstname = "Петр" + String.valueOf(20+i);
                op.fathername = "Артемьевич" + String.valueOf(20+i);


                op.org_kod =  torStructDictList.get(0);
                op.orgname = "РУП \"Брестское агентство по государственной регистрации и земельному кадастру\"";
                oService.addSubject(op);

            }

            List<OPerson> result_o= oService.findOffUser("Иванов22", null , "Артем", null, "БРЕСТ",null);


            int i = 0;

            for(Person o : result_o)
            {
                OPerson op = (OPerson) o;
                op.surname = "Иванов" + String.valueOf(40+i);
                op.firstname  = "Петр" + String.valueOf(40+i) ;
                op.fathername = "Артемьевич" + String.valueOf(40+i);

                oService.refreshSubject(op);
                i++;
            }

        } catch (DaoException e) {

            retval = false;

            e.printStackTrace();
        }

        assertTrue(retval);
    }


    public void AddJurSubject(){

        log.info("Invoked SubjectTest.AddJurSubject" );

        boolean retval = true;

        try {

            for(int i=0; i<10; i++) {

                JPerson jp = new JPerson();
                jp.subjectdataid = Integer.valueOf(scg.generate("SEQ_SUBJECTSDATA_ID.nextval").toString());
                jp.isOwner = 1;

                jp.subjectType = subjectServDictList.get(9);
                jp.orgRightForm =  orgStructDictList.get(6);

                jp.fullname = "Валенки"+"_"+i;
                jp.regNumber = String.valueOf(123456000+i);
                jp.unp = String.valueOf(159357258+i);
                jp.bothRegDate = new Date();
                jService.addSubject(jp);

            }

        } catch (DaoException e) {

            e.printStackTrace();

            retval = false;
        }

        assertTrue(retval);

    }

    public void GetJurSubject()  {

        log.info("Invoked SubjectTest.GetJurSubject" );

        boolean retval = true;

        List<JPerson> result_j_0 = jService.getAll();//jService.findByNameType("Вал", "", subjectServDictList.get(9).getCode_id());

        List<JPerson> result_j_1 = jService.findByNameType("", "",null);

        JPerson dc = jService.getSubject((Integer)19126);

        assertTrue(!result_j_0.isEmpty());


    }

    public void UpdateJurSubject()  {

        log.info("Invoked SubjectTest.UpdateJurSubject" );

        boolean retval = true;

        try {

        for(int i=0; i<10; i++) {
            JPerson jp = new JPerson();
            jp.subjectdataid = Integer.valueOf(scg.generate("SEQ_SUBJECTSDATA_ID.nextval").toString());
            jp.fullname = "ОАО Update_" + String.valueOf(i) ;
            jp.orgRightForm =  orgStructDictList.get(6);
            jp.subjectType = subjectServDictList.get(14);
            jp.regNumber = String.valueOf(124566000+i) ;
            jp.unp = String.valueOf(159777758+i);
            jp.bothRegDate = new Date();
            jService.addSubject(jp);
        }


        List<JPerson> result_p= jService.findByNameType("Upd", null, subjectServDictList.get(14).getCode_id());

        int i = 10;

        for(Person p : result_p)
        {
            JPerson jp = (JPerson) p;
            jp.fullname  = "ОАО Update_" + String.valueOf(i) ;
            jp.unp =  String.valueOf(159777758+i);
            jp.regNumber =  String.valueOf(124566000+i);

            jp.orgRightForm =  orgStructDictList.get(7);
            jp.subjectType = subjectServDictList.get(12);

            jService.refreshSubject(jp);
            i++;
        }

        } catch (DaoException e) {

            retval = false;

            e.printStackTrace();
        }

        assertTrue(retval);
    }


    public void AddPhysSubject() {

        log.info("Invoked SubjectTest.AddPhysSubject" );

        boolean retval = true;

        try {

            for(int i=0; i<10; i++) {

                PPerson pp = new PPerson();
                pp.subjectdataid = Integer.valueOf(scg.generate("SEQ_SUBJECTSDATA_ID.nextval").toString());
                pp.surname = "Иван"+"_"+i;
                pp.firstname = "Иванов"+"_"+i;
                pp.fathername = "Иванович"+"_"+i;
                pp.subjectType = subjectServDictList.get(2);
                pp.isOwner = 1;
                pp.bothRegDate = new Date();
                pp.personalNumber = "78"+String.valueOf(71000+i)+"F408AE8" ;
                //pp.personalNumber += (String)scg.generate("SUBJECTS_PKG.GET_PN_CHECKDIGIT('"+pp.personalNumber+"0')");
                pp.sitizens = stateDictList.get(73);

                pService.addSubject(pp);

            }

        } catch (DaoException e) {

            e.printStackTrace();

            retval = false;
        }

        assertTrue(retval);

    }

    public void GetPhysSubject() {

        log.info("Invoked SubjectTest.GetPhysSubject()" );

        boolean retval = true;

        List<PPerson> result_p = pService.findByFIOType("Ив", "И", null, null, subjectServDictList.get(2).getCode_id());

        assertTrue(!result_p.isEmpty());

    }

    public void UpdatePhysSubject() {

        log.info("Invoked SubjectTest.UpdatePhysSubject" );

        boolean retval = true;

        try {

        for(int i=0; i<10; i++) {

            PPerson pp = new PPerson();
            pp.subjectdataid = Integer.valueOf(scg.generate("SEQ_SUBJECTSDATA_ID.nextval").toString());

            pp.surname = "Дженкинс"+"_"+i;
            pp.firstname = "Владимир"+"_"+i;
            pp.fathername = "Обамович"+"_"+i;
            pp.subjectType = subjectServDictList.get(3);
            pp.isOwner = 1;
            pp.sitizens = stateDictList.get(2);
            pp.bothRegDate = new Date();
            pp.datestart = new Date();

            pp.personalNumber = "78"+String.valueOf(31158+i)+"F408AE" ;
            pp.personalNumber += (String)scg.generate("SUBJECTS_PKG.GET_PN_CHECKDIGIT('"+pp.personalNumber+"0')");

            pService.addSubject(pp);

        }

        List<PPerson> result_p= pService.findByFIOType("Дж", "", "Об", "311", subjectServDictList.get(3).getCode_id());

        int i = 0;

        for(Person p : result_p)
        {
            PPerson pp = (PPerson) p;

            pp.surname  += String.valueOf(i);

            pp.personalNumber = "78"+String.valueOf(31158+i)+"F408AE" ;
            pp.personalNumber += (String)scg.generate("SUBJECTS_PKG.GET_PN_CHECKDIGIT('"+pp.personalNumber+"0')");

            pp.subjectType = subjectServDictList.get(2);
            pp.sitizens = stateDictList.get(3);

            pService.refreshSubject(pp);
            i++;
        }

        } catch (DaoException e) {
            e.printStackTrace();
        }


    }


    public int CheckLastNumber(String str) {

        int v_1 = Integer.valueOf(str.substring(1, 2));
        int v_2 = Integer.valueOf(str.substring(2, 3));
        int v_3 = Integer.valueOf(str.substring(3, 4));

        int v_4 = Integer.valueOf(str.substring(4, 5));
        int v_5 = Integer.valueOf(str.substring(5, 6));
        int v_6 = Integer.valueOf(str.substring(6, 7));
        int v_7 = Integer.valueOf(str.substring(7, 8));

        int v_8 = Integer.valueOf(str.substring(9, 10));
        int v_9 = Integer.valueOf(str.substring(10, 11));
        int v_10 = Integer.valueOf(str.substring(11, 12));

        int p_ret = ((v_1*7+ v_2*3+ v_3*1+ v_4*7+ v_5*3+ v_6*1+ v_7*7+ v_8*3+ v_9*1+v_10*7)% 10);

        return p_ret;
    }

}
