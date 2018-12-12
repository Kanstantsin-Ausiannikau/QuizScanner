using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace BL
{
    public class User
    {
        public int Id { get; set; }
        public string UserName { get; set; }
        public string Password { get; set; }
        public DateTime LastSyncTime { get; set; }

        public string LastRespondentNumber { get; set; }

        public string LastQuizNumber { get; set; }

        public ICollection<Group> Groups { get; set; }

        public ICollection<Quiz> Quizs { get; set; }

        public User()
        {
            LastQuizNumber = "1111";
            LastRespondentNumber = "1111";
            
            Groups = new List<Group>();

            Quizs = new List<Quiz>();
        }
    }
}