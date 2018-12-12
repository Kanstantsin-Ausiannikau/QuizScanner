using Microsoft.AspNet.Identity.Owin;
using Newtonsoft.Json;
using Quizzy.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.SqlClient;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using BL;

namespace Quizzy.Controllers
{
    public class AndroidController : Controller
    {
        QuizzyContext db = new QuizzyContext();

        // GET: Android
        public ActionResult Index()
        {
            return View();
        }

        [HttpGet]
        public string GetRequest()
        {
            return "10";
        }

        [HttpPost]
        public async Task<string> HelloRequest()
        {
            return (await IsVerifiedUser(Request.Params["user"], Request.Params["password"])) ? "1" : "-1";
        }

        [HttpPost]
        public async Task<string> GetQuizesRequest()
        {
            string userName = Request.Params["user"];
            string password = Request.Params["password"];
            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).Include("Quizs.Questions.Answers").FirstOrDefaultAsync();

            StringBuilder sb = new StringBuilder();

            if (user != null)
            {
                string separator = "";
                foreach (var quiz in user.Quizs)
                {
                    if (!quiz.IsDeleted)
                    {
                        quiz.AnswerText = Quiz.SerializeAnswers(quiz.Questions.ToList());
                        //Удалить свойство AnswerText? или обновлять его при редактировании вопроса.

                        sb.Append($"{separator}{0};{quiz.Id};{quiz.ParentQuizId};{quiz.QuizNumber};{quiz.Title};{quiz.QuizVersion};{quiz.AnswerText}");
                        separator = "|";
                    }
                }
            }

            return sb.ToString();
        }

        [HttpPost]
        public async Task<string> GetRespondentsRequest()
        {
            var user = await GetUser();

            if (user != null)
            {
                StringBuilder sb = new StringBuilder();
                string separator = "";
                foreach (var group in user.Groups)
                {
                    foreach (var respondent in group.Respondents)
                    {
                        if (!respondent.IsDeleted)
                        {
                            sb.Append($"{separator}{respondent.Id};{group.Id};{respondent.RespondentNumber};{respondent.FirstName};{respondent.LastName}");
                            separator = "|";
                        }
                    }
                }
                return sb.ToString();
            }
            return null;
        }

        public async Task<string> GetRespondentsConfirmationRequest()
        {
            string userName = Request.Params["user"];
            string password = Request.Params["password"];

            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).Include("Groups.Respondents").FirstOrDefaultAsync();

            string[] data = Request.Params["respondents"].Split(new char[] { '|' }, StringSplitOptions.RemoveEmptyEntries);

            for (int i = 0; i < data.Length; i++)
            {
                string[] respondentData = data[i].Split(new char[] { ';' }, StringSplitOptions.RemoveEmptyEntries);
                int serverId = int.Parse(respondentData[1]);
                int clientId = int.Parse(respondentData[0]);

                Respondent respondent = db.Respondents.First(r => r.Id == serverId);

                if (respondent.IsModified)
                {
                    respondent.ClientId = clientId;
                    respondent.IsModified = false;
                    db.Entry(respondent).State = EntityState.Modified;
                }
            }

            //var ans = db.Groups.Include("Quizs");

            await db.SaveChangesAsync();

            return "Ok";
        }

        [HttpPost]
        public async Task<string> GetGroupsRequest()
        {
            string userName = Request.Params["user"];
            string password = Request.Params["password"];

            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).Include("Groups").FirstOrDefaultAsync();

            //string groups = JsonConverter.GetGroupsJsonData(user.Groups);

            //StringBuilder sb = new StringBuilder();
            //string separator = "";
            //foreach (var group in user.Groups)
            //{
            //    if (!group.IsDeleted)
            //    {
            //        sb.Append($"{separator}{0};{group.Id};{group.GroupName}");
            //        separator = "|";
            //    }
            //}
            //return sb.ToString();

            return JsonConvert.SerializeObject(user.Groups);
        }

        public async Task<string> GetGroupsQuizesRequest()
        {
            string userName = Request.Params["user"];
            string password = Request.Params["password"];

            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).Include("Groups.Quizs").FirstOrDefaultAsync();

            StringBuilder sb = new StringBuilder();
            string separator = "";
            foreach (var group in user.Groups)
            {
                foreach (var quiz in group.Quizs)
                {
                    sb.Append($"{separator}{group.Id};{quiz.Id}");
                    separator = "|";
                }
            }

            return sb.ToString();
        }

        public async Task<string> GetGroupsQuizesConfirmationRequest()
        {
            string userName = Request.Params["user"];
            string password = Request.Params["password"];

            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).Include("Groups.Quizs").FirstOrDefaultAsync();

            return "Ok";
        }

        public async Task<string> GetQuizesConfirmationRequest()
        {
            string userName = Request.Params["user"];
            string password = Request.Params["password"];

            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).Include("Quizs").FirstOrDefaultAsync();

            string[] data = Request.Params["quizs"].Split(new char[] { '|' }, StringSplitOptions.RemoveEmptyEntries);

            for (int i = 0; i < data.Length; i++)
            {
                string[] groupData = data[i].Split(new char[] { ';' }, StringSplitOptions.RemoveEmptyEntries);
                int serverId = int.Parse(groupData[1]);
                int clientId = int.Parse(groupData[0]);

                Quiz quiz = db.Quizes.First(q => q.Id == serverId);

                if (quiz.IsModified)
                {
                    quiz.ClientId = clientId;
                    quiz.IsModified = false;
                    db.Entry(quiz).State = EntityState.Modified;
                }
            }
            await db.SaveChangesAsync();

            return "Ok";
        }

        public async Task<string> GetGroupsConfirmationRequest()
        {
            string userName = Request.Params["user"];
            string password = Request.Params["password"];

            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).Include("Groups").FirstOrDefaultAsync();

            string[] data = Request.Params["groups"].Split(new char[] { '|' }, StringSplitOptions.RemoveEmptyEntries);

            for (int i = 0; i < data.Length; i++)
            {
                string[] groupData = data[i].Split(new char[] { ';' }, StringSplitOptions.RemoveEmptyEntries);
                int serverId = int.Parse(groupData[1]);
                int clientId = int.Parse(groupData[0]);

                Group group = db.Groups.First(g => g.Id == serverId);

                if (group.IsModified)
                {
                    group.ClientId = clientId;
                    group.IsModified = false;
                    db.Entry(group).State = EntityState.Modified;
                }

            }

            await db.SaveChangesAsync();

            return "Ok";
        }

        [HttpPost]
        public async Task<string> SetAnswersRequest()
        {
            string[] answersData = Request.Params["answers"].Split(new char[] { '|' }, StringSplitOptions.RemoveEmptyEntries);

            foreach (string a in answersData)
            {
                const int RESPONDENT_NUMBER = 2;

                string[] data = a.Split(new char[] { ';' }, StringSplitOptions.RemoveEmptyEntries);

                Respondent r = db.Respondents.Where(rr => rr.RespondentNumber == data[RESPONDENT_NUMBER]).Include("Answers").FirstOrDefault();

                Quiz q = await db.Quizes.Where(qq => qq.QuizNumber == data[1]).Include("Answers").FirstOrDefaultAsync();

                Answer answer = new Answer()
                {
                    AnswerText = data[3],
                    Date = DateTime.ParseExact(data[4], "ddd MMM dd HH:mm:ss 'GMT'K yyyy", CultureInfo.InvariantCulture),
                    IsSync = true
                };

                r.Answers.Add(answer);

                q.Answers.Add(answer);

                db.Entry(r).State = EntityState.Modified;
                db.Entry(q).State = EntityState.Modified;
            }

            return db.SaveChanges().ToString();
        }

        private async Task<bool> IsVerifiedUser(string userName, string password)
        {
            var user = await db.Users.Where(u => (u.UserName == userName) && (u.Password == password)).FirstOrDefaultAsync();

            return user != null;
        }

        private async Task<User> GetUser()
        {
            string name = Request.Params["user"];
            string password = Request.Params["password"];

            if (name != null && password != null)
            {

                var manager = HttpContext.GetOwinContext().Get<ApplicationSignInManager>();

                var result = manager.PasswordSignIn(name, password, false, shouldLockout: false);

                User user = null;

                if (result == SignInStatus.Success)
                {

                    var users = db.Users.Include("Groups.Respondents").Include("Quizs").Include("Groups.Quizs");

                    user = await users.Where(u => u.UserName == name).FirstOrDefaultAsync();
                }

                return user;
            }

            return null;
        }
    }
}