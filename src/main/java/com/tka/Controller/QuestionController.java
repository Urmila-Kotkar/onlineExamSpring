package com.tka.Controller;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tka.entity.Answer;
import com.tka.entity.Question;
import com.tka.entity.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin("http://localhost:4200")
public class QuestionController 
{


	@Autowired
	SessionFactory factory;

	// localhost:8080/getFirstQuestion/java
	
	@GetMapping("getFirstQuestion/{subject}")
	public Question getFirstQuestion(@PathVariable String subject)
	{
		// select * from question where subject=java
		
		Session session=factory.openSession();
		
		CriteriaBuilder cb=session.getCriteriaBuilder();
				
		CriteriaQuery<Question> cr=cb.createQuery(Question.class);
			
		Root<Question>  entityobject=cr.from(Question.class);// decide table
		
		cr.select(entityobject);// decide columns
		
		cr.where(cb.equal(entityobject.get("subject"),subject));// write condition
		
		Query<Question> query=session.createQuery(cr);
					
		List<Question> list=query.list();
		
		Question question=list.get(0);
		
		HttpSession httpsession=LoginController.httpsession;
		
		httpsession.setAttribute("allquestion",list); // add questions in session
		
		return question;
		
	}
	//0 1 2
	@GetMapping("nextQuestion")
	public Question nextQuestion()
	{
		
		HttpSession httpsession=LoginController.httpsession;
		
		List<Question> list=(List<Question>) httpsession.getAttribute("allquestion");
				
		int index=(int) httpsession.getAttribute("questionIndex");//0 1 2
		Question question=null;
		//0 1       3-1=2           2 condition false 2<2
		if(index < list.size()-1) {
			
			int newindex=index+1;//0+1=1 1+1=2
			
			httpsession.setAttribute("questionIndex",newindex);//3
			
			 question=list.get(newindex);//1 2
		}
		// 2<2
		else {
			question=list.get(list.size()-1);//3-1=2
		}
		
		
		
		return question;
		
	}	
	
     	//0 1 2
		@GetMapping("previousQuestion")
		public Question previousQuestion()
		{
			
			HttpSession httpsession=LoginController.httpsession;
			
			List<Question> list=(List<Question>) httpsession.getAttribute("allquestion");
					
			int index=(int) httpsession.getAttribute("questionIndex");// 2 1 0
			Question question=null;
			//2 1
			if(index>0) {
				
				int newindex=index-1;//2-1=1  1-1=0
				
				httpsession.setAttribute("questionIndex",newindex);//1 0
				
				 question=list.get(newindex);//1 0
			}
			// 0>0
			else {
				question=list.get(0);//0
			}
			return question;
			
		}	
	//	{"qno":1, "qtext":"what is 2+2?","submittedAnswer":"4","correctAnswer":"4"}
		@PostMapping("saveAnswer")
		public void saveAnswer(@RequestBody Answer answer)
		{
			 

			HttpSession httpsession=LoginController.httpsession;
			
			HashMap<Integer, Answer> hashmap= (HashMap<Integer, Answer>) httpsession.getAttribute("submittedDetails");
			hashmap.put(answer.qno, answer);
			System.out.println(hashmap);
		}
		
		@GetMapping("allAnswers")
		public Collection<Answer> getAllAnswer() {
			
			HttpSession httpsession=LoginController.httpsession; 
			HashMap<Integer, Answer> hashmap=(HashMap<Integer, Answer>) httpsession.getAttribute("submittedDetails");
			Collection<Answer> allanswers=hashmap.values();
            return allanswers;
		}

		@GetMapping("getAllQno/{subject}")
		public List<Integer> getAllQno(@PathVariable String subject)
		{
			
			Session session=factory.openSession();
			
			Query query=session.createQuery("select distinct qno from Question where subject=:subject");
			
			query.setParameter("subject",subject);
			
			List<Integer> list=query.list();
			
			return list;
					
		}
		
		@GetMapping("calculateScore")
		public int calculateScore() {
			HttpSession httpSession=LoginController.httpsession;
			
			HashMap<Integer, Answer> hashMap=(HashMap<Integer, Answer>) httpSession.getAttribute("submittedDetails");
			Collection<Answer> allAnswers=hashMap.values();
			httpSession.setAttribute("score", 0);
			for (Answer answer : allAnswers) {
				
				if(answer.submittedAnswer.equals(answer.correctAnswer)) {
					httpSession.setAttribute("score",(int) httpSession.getAttribute("score")+1);
				}
			}
			int score=(int) httpSession.getAttribute("score");
			return score;
		}
	
		@GetMapping("getAllSubjects")
		public List<String> getAllSubjects()
		{
			
			Session session=factory.openSession();
			
			Query query=session.createQuery("select distinct subject from Question");
			
			List<String> list=query.list();
			
			return list;
					
		}
		
		@GetMapping("getQuestion/{questionNumber}")
		public Question getQuestion(@PathVariable int questionNumber)
		{
			HttpSession httpSession=LoginController.httpsession;
			
			List<Question> listofquestions=(List<Question>) httpSession.getAttribute("allquestion");
			
			Question expectedQuestion=null;
			
			for (Question question : listofquestions) 
			{
				if(question.qno==questionNumber)
				{
					expectedQuestion=question;
				}
			}
			
			return expectedQuestion;
			
		}
}











