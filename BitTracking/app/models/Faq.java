package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Constraint;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emina on 16.10.2015.
 */
@Entity
public class Faq extends Model {

    @Id
    public Long id;
    @Constraints.MaxLength(255)
    @Constraints.MinLength(value = 0, message = "Question field is required!")
    @Column(columnDefinition = "Text")
    public String question;
    @Constraints.Required(message = "Please add an answer here.")
    @Column(columnDefinition = "Text")
    @Constraints.MinLength(4)
    public String answer;

    public static Finder<Long, Faq> faqFinder = new Finder<>(Faq.class);


    public Faq(){
    }
    /**
     * Constructor with question and string arguments
     * @param question
     * @param answer
     */
    public Faq(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    /**
     * DeleteFaq method will delete Faq with given Id
     * @param id
     */
    public static void deleteFaq(Long id){
        Faq.faqFinder.deleteById(id);
    }

    /**
     * AllFaqs method will return all FAQ as a lisr
     * @return
     */
    public static List<Faq> allFaqs(){
        List<Faq> faqList = faqFinder.findList();
        if (faqList.isEmpty()){
            faqList = new ArrayList<>();
        }
        return faqList;
    }

    /**
     * Method will find Faq by id
     * @param id
     * @return Faq
     */
    public static Faq findById(Long id){
        Faq faq = Faq.faqFinder.where().eq("id", id).findUnique();
        return faq;
    }
}
