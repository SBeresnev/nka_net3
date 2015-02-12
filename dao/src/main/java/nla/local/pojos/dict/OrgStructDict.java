package nla.local.pojos.dict;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Created by beresnev on 05.02.2015.
 * Organizational structure dictionary (ОАО ЗАО ....)
 */

@Entity
@Immutable
@Table(name="ANALYTICCODES")
@Where(clause = "ANALYTIC_TYPE = 220")
public class OrgStructDict extends Dict implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANALYTIC_CODE", unique = true, nullable = false)
    private Integer code_id;

    @Column( name = "ANALYTIC_CODE_NAME")
    private String code_name;

    @Column( name = "ANALYTIC_CODE_SHORTNAME")
    private String code_short_name;

  /*
    @Column( name = "PARENT_CODE")
    private  Integer parent_code;

    @Override
    public Integer getParent_code() { return parent_code; }

    @Override
    public void setParent_code(Integer parent_code) {
        this.parent_code = parent_code;
    }
    */

    @Override
    public Integer getCode_id() {
        return code_id;
    }

    @Override
    public String getCode_name() {
        return code_name;
    }

    @Override
    public String getCode_short_name() {
        return code_short_name;
    }

    @Override
    public void setCode_id(Integer code_id) {
        this.code_id = code_id;
    }

    @Override
    public void setCode_name(String code_name) {
        this.code_name = code_name;
    }

    @Override
    public void setCode_short_name(String code_short_name) {
        this.code_short_name = code_short_name;
    }

    @Override
    public Class getType()
    {
        return OrgStructDict.class;

    }

}
