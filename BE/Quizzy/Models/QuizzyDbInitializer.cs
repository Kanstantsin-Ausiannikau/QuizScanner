using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Web;

namespace Quizzy.Models
{
    public class QuizzyDbInitializer : DropCreateDatabaseAlways<QuizzyContext>
    {
        protected override void Seed(QuizzyContext context)
        {

            Answer a1 = new Answer() { IsSync = true, Date = DateTime.Now, AnswerText = "1,2,3,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" };
            context.Answers.Add(a1);
            context.Answers.Add(new Answer() { IsSync = true, Date = DateTime.Now, AnswerText = "1,2,3,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" });
            //context.Answers.Add(new Answer() { QuizId = 1, IsSync = true, Date = DateTime.Now, AnswerText = "1,2,3,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" });

            Quiz q1 = new Quiz() { Title = "Основы ООП", QuizNumber = "1000", QuizVersion = 1, AnswerText = "1,2,3,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0", Answers = new List<Answer>() { a1 } };
            context.Quizes.Add(q1);
            context.Quizes.Add(new Quiz() { Title = "Основы программирования", QuizNumber = "1002", QuizVersion = 1, AnswerText = "1,2,3,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" });
            context.Quizes.Add(new Quiz() { Title = "Основы ООП1", QuizNumber = "1122", QuizVersion = 1, AnswerText = "1,2,3,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" });



            Respondent r1 = new Respondent() { RespondentNumber = "1000", FirstName = "Иван", LastName = "Иванов", Answers = new List<Answer>() { a1 } };
            context.Respondents.Add(r1);
            context.Respondents.Add(new Respondent() { RespondentNumber = "1001", FirstName = "Петр", LastName = "Петров" });
            context.Respondents.Add(new Respondent() { RespondentNumber = "1357", FirstName = "Тест", LastName = "Тестович" });



            Group g1 = context.Groups.Add(new Group() { GroupName = "ПИР-171", IsDeleted = false, IsModified = true, Respondents = new List<Respondent>() { r1 }, Quizs = new List<Quiz>() { q1 } });
            context.Groups.Add(g1);


            User u1 = new User() { Id = 1, UserName = "User", Password = "Password", Groups = new List<Group>() { g1 } };
            context.Users.Add(new User() { Id = 1, UserName = "Test", Password = "12345" });
            context.Users.Add(u1);


            base.Seed(context);
        }
    }
}