using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Web;

namespace BL
{
    public class Quiz
    {
        //int[] answers;

        public int Id { get; set; }

        public int ClientId { get; set; }

        public int ParentQuizId { get; set; }

        [Display(Name ="Название теста")]
        public string Title { get; set; }

        public string AnswerText { get; set; }
        [Display(Name="Номер теста" )]
        public string QuizNumber { get; set; }

        public int QuizVersion { get; set; }

        public bool IsModified { get; set; }

        public bool IsDeleted { get; set; }

        public Quiz()
        {
            //answers = new int[30];

            Questions = new List<Question>();

            Groups = new List<Group>();

            Answers = new List<Answer>();
        }

        public ICollection<Question> Questions { get; set; }

        public ICollection<Group> Groups { get; set; }

        public ICollection<Answer> Answers { get; set; }

        //public void DeserializeAnswers(string text)
        //{
        //    string[] data = text.Split(new char[] { ',' }, StringSplitOptions.RemoveEmptyEntries);

        //    for (int i = 0; i < answers.Length; i++)
        //    {
        //        answers[i] = int.Parse(data[i]);
        //    }
        //}

        public static string SerializeAnswers(List<Question> questions)
        {
            StringBuilder sb = new StringBuilder();

            string questionSeparator = "";

            foreach (var q in questions)
            {
                sb.Append(questionSeparator);
                string separator = "";
                foreach (var a in q.Answers)
                {
                    sb.Append(separator);
                    if (a.RightAnswer)
                    {
                        sb.Append(q.Difficulty);
                    }
                    else
                    {
                        sb.Append("0");
                    }
                    separator = ",";
                }
                questionSeparator = "%";
            }

            return sb.ToString();
        }

        public static Quiz GetRandomizeQuiz(Quiz currentQuiz, string quizNumber)
        {
            Quiz quiz = currentQuiz.Copy(quizNumber);

            Random rnd = new Random((int)DateTime.Now.Ticks);

            var questions = currentQuiz.Questions.ToList();

            for(int i = 0;i<currentQuiz.Questions.Count;i++)
            {
                var index = rnd.Next(0, questions.Count);

                for (int j = 0; j < 5 * 5; j++)
                {
                    int aIndex = rnd.Next(0, 3);
                    if (questions[index].Answers[aIndex]!=null&& questions[index].Answers[aIndex+1]!=null)
                    {
                        var temp = questions[index].Answers[aIndex];
                        questions[index].Answers[aIndex] = questions[index].Answers[aIndex + 1];
                        questions[index].Answers[aIndex + 1] = temp;
                    }
                }

                quiz.Questions.Add(
                    new Question() {
                        Difficulty = questions[index].Difficulty,
                        QuestionText = questions[index].QuestionText,
                        Answers = new List<QuestionAnswer>() {
                            new QuestionAnswer(){Answer = questions[index].Answers[0].Answer, RightAnswer = questions[index].Answers[0].RightAnswer },
                            new QuestionAnswer(){Answer = questions[index].Answers[1].Answer, RightAnswer = questions[index].Answers[1].RightAnswer },
                            new QuestionAnswer(){Answer = questions[index].Answers[2].Answer, RightAnswer = questions[index].Answers[2].RightAnswer },
                            new QuestionAnswer(){Answer = questions[index].Answers[3].Answer, RightAnswer = questions[index].Answers[3].RightAnswer },
                            new QuestionAnswer(){Answer = questions[index].Answers[4].Answer, RightAnswer = questions[index].Answers[4].RightAnswer } } });

                questions.RemoveAt(index);
            }
            return quiz;
        }

        internal Quiz Copy(string quizNumber)
        {
            return new Quiz() {
                ParentQuizId = this.Id,
                Id = 0,
                QuizNumber = quizNumber,
                Title = $"{this.Title} (Копия {this.QuizNumber})", IsModified = true };
        }
    }
}