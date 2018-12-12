using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace BL
{
    public class QuestionAnswer
    {
        public int Id { get; set; }
        public bool RightAnswer { get; set; }
        public string Answer { get; set; }
    }
}