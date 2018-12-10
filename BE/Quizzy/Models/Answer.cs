using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Quizzy.Models
{
    public class Answer
    {
        public int Id { get; set; }

        public int ClientId { get; set; }

        public string AnswerText { get; set; }

        public DateTime Date { get; set; }

        public bool IsSync { get; set; }
    }
}