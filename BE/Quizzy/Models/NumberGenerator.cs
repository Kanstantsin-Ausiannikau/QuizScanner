using System.Text;
using BL;
using Quizzy.DAL;

namespace Quizzy.Models
{
    public class NumberGenerator
    {
        public static string GetRespondentNumber(User user)
        {
            QuizzyContext db = new QuizzyContext();

            string nextNumber = GetNextRespondentNumber(user.LastRespondentNumber);

            user.LastRespondentNumber = nextNumber;

            db.Entry(user).State = System.Data.Entity.EntityState.Modified;

            db.SaveChanges();

            return $"A{nextNumber[0]}B{nextNumber[1]}C{nextNumber[2]}D{nextNumber[3]}";
        }

        private static string GetNextRespondentNumber(string lastRespondentNumber)
        {
            /*   int[] respondentNumber = new int[lastRespondentNumber.Length];
               for (int i=0;i<lastRespondentNumber.Length;i++)
               {
                   respondentNumber[i] = int.Parse(lastRespondentNumber[i].ToString());
               }
               respondentNumber[lastRespondentNumber.Length-1]++;
               for (int i = respondentNumber.Length-1;i>0;i--)
               {
                   if (respondentNumber[i] >= 10)
                   {
                       respondentNumber[i]= respondentNumber[i] - 10;
                       respondentNumber[i - 1] = respondentNumber[i - 1] + 1;
                   }
                   else
                   {
                       break;
                   }

               }
               StringBuilder sb = new StringBuilder();
               foreach(var item in respondentNumber)
               {
                   sb.Append(item);
               }

               return sb.ToString(); */

            return GetNextNumber(lastRespondentNumber);
        }

        public static string GetQuizNumber(User user)
        {
            QuizzyContext db = new QuizzyContext();

            string nextNumber = GetNextQuizNumber(user.LastQuizNumber);

            user.LastQuizNumber = nextNumber;

            db.Entry(user).State = System.Data.Entity.EntityState.Modified;

            db.SaveChanges();

            return $"A{nextNumber[0]}B{nextNumber[1]}C{nextNumber[2]}D{nextNumber[3]}";
        }

        private static string GetNextQuizNumber(string lastQuizNumber)
        {
            /*  int[] quizNumber = new int[lastQuizNumber.Length];
              for (int i = 0; i < lastQuizNumber.Length; i++)
              {
                  quizNumber[i] = int.Parse(lastQuizNumber[i].ToString());
              }
              quizNumber[lastQuizNumber.Length - 1]++;
              for (int i = quizNumber.Length - 1; i > 0; i--)
              {
                  if (quizNumber[i] > 5)
                  {
                      quizNumber[i] = quizNumber[i] - 5;
                      quizNumber[i - 1] = quizNumber[i - 1] + 1;
                  }
                  else
                  {
                      break;
                  }

              }
              StringBuilder sb = new StringBuilder();
              foreach (var item in quizNumber)
              {
                  sb.Append(item);
              }

              return sb.ToString();*/

            return GetNextNumber(lastQuizNumber);
        }

        private static string GetNextNumber(string lastNumber)
        {
            int[] number = new int[lastNumber.Length];
            for (int i = 0; i < lastNumber.Length; i++)
            {
                number[i] = int.Parse(lastNumber[i].ToString());
            }
            number[lastNumber.Length - 1]++;
            for (int i = number.Length - 1; i > 0; i--)
            {
                if (number[i] > 7)
                {
                    number[i] = number[i] - 7;
                    number[i - 1] = number[i - 1] + 1;
                }
                else
                {
                    break;
                }

            }
            StringBuilder sb = new StringBuilder();
            foreach (var item in number)
            {
                sb.Append(item);
            }

            return sb.ToString();
        }
    }
}