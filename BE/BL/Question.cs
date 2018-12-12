using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace BL
{
    public class Question
    {
        public int Id { get; set; }
        [Display(Name = "Сложность вопроса")]
        public int Difficulty { get; set; }

        [Display(Name = "Текст вопроса")]
        public string QuestionText { get; set; }

        //public int RightAnswerNumber { get; set; }

        public List<QuestionAnswer> Answers { get; set; }

        public Question()
        {
            Answers = new List<QuestionAnswer>();
        }
     }
}