using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace Quizzy.Models
{
    public class Group
    {
        public int Id { get; set; }

        public int ClientId { get; set; }

        [Display(Name = "Группа")]
        public string GroupName { get; set; }

        public bool IsModified { get; set; }

        public bool IsDeleted { get; set; }

        public ICollection<Quiz> Quizs { get; set; }

        public ICollection<Respondent> Respondents { get; set; }

        public Group()
        {
            Quizs = new List<Quiz>();

            Respondents = new List<Respondent>();
        }
    }
}