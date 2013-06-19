package dto;

import play.data.validation.Email;
import play.data.validation.Required;

/**
 * DTO for a user object used to transfer the 
 * data between controllers and views.
 *  
 * @author Paviel Zakiervasevic
 */
public class UserDTO {
	
	private String name;
	private String email;
	private String password;
	private String question;
	private String answer;
	
	public UserDTO(String name, String email, String password, String question,
			String answer) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.question = question;
		this.answer = answer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

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

}
