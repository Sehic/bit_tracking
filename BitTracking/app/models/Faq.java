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
    @Constraints.Required(message = "Please add a question here.")
    @Constraints.MinLength(value = 0, message = "Question field is required!")
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

    /** Getters and setters**/

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Create method creates a new FAQ with question and answer
     * @param question
     * @param answer
     * @return a new Faq id
     */
    public static Long createFaq(String question, String answer){
        Faq newFaq = new Faq(question, answer);
        newFaq.save();
        return newFaq.id;
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
     * This method will check if FAQ exist
     * @param question
     * @return true or false
     */
    public static boolean checkFaqQuestion(String question){
        return faqFinder.where().eq("question", question).findUnique()!=null;
    }

    /**
     * Method will fiond Faq by id
     * @param id
     * @return Faq
     */
    public static Faq findById(Long id){
        return faqFinder.byId(id);
    }
}
