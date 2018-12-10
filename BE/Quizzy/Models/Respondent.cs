using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace Quizzy.Models
{
    public class Respondent
    {
        public int Id { get; set; }

        public int ClientId { get; set; }

        [Display(Name ="Номер тестируемого")]
        public string RespondentNumber { get; set; }

        [Display(Name ="Имя")]
        public string FirstName { get; set; }

        [Display(Name = "Фамилия")]
        public string LastName { get; set; }

        public bool IsModified { get; set; }

        public bool IsDeleted { get; set; }

        public ICollection<Answer> Answers { get; set; }

        public Respondent()
        {
            Answers = new List<Answer>();
        }
    }
}