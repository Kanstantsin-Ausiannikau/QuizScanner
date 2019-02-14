using Quizzy.Models;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using System.Web.Mvc;
using System;
using iTextSharp.text;
using System.IO;
using iTextSharp.text.pdf;
using System.Web;
using OfficeOpenXml;
using System.Diagnostics;
using BL;
using Quizzy.DAL;

namespace Quizzy.Controllers
{
    [Authorize]
    public class QuizsController : Controller
    {
        private QuizzyContext db = new QuizzyContext();

        // GET: Quizs
        public async Task<ActionResult> Index()
        {
            var user = await db.Users.Where(u => u.UserName == User.Identity.Name).Include("Quizs").FirstOrDefaultAsync();

            var quizs = user.Quizs.Where(q => q.IsDeleted == false).ToList();

            return View(quizs);
        }


        // GET: Quizs/Details/5
        public async Task<ActionResult> Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Quiz quiz = await db.Quizes.FindAsync(id);
            if (quiz == null)
            {
                return HttpNotFound();
            }
            return View(quiz);
        }

        // GET: Quizs/Create
        public async Task<ActionResult> Create()
        {
            var user = await db.Users.Where(u => u.UserName == User.Identity.Name).FirstOrDefaultAsync();

            Quiz q = new Quiz() { QuizNumber = NumberGenerator.GetQuizNumber(user), Title="Новый тест", IsModified  =true };
            return View(q);
        }

        // POST: Quizs/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Create([Bind(Include = "Id,ClientId,Title,AnswerText,QuizNumber,QuizVersion,IsModified,IsDeleted,Questions")] Quiz quiz)
        {
            return await AddQuiz(quiz);
        }

        private async Task<ActionResult> AddQuiz(Quiz quiz)
        {
            if (ModelState.IsValid)
            {
                db.Entry(quiz).State = EntityState.Added;


                quiz.AnswerText = Quiz.SerializeAnswers(quiz.Questions.ToList());

                foreach (var q in quiz.Questions)
                {
                    db.Entry(q).State = EntityState.Added;
                    foreach (var a in q.Answers)
                    {
                        db.Entry(a).State = EntityState.Added;
                    }
                }

                User user = await db.Users.Where(u => u.UserName == User.Identity.Name).FirstOrDefaultAsync();
                user.Quizs.Add(quiz);

                db.Entry(user).State = EntityState.Modified;
                db.Quizes.Add(quiz);
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }

            return View(quiz);
        }

        [HttpGet]
        public async Task<ActionResult> Import()
        {
            var user = await db.Users.Where(u => u.UserName == User.Identity.Name).FirstOrDefaultAsync();

            Quiz q = new Quiz() { QuizNumber = NumberGenerator.GetQuizNumber(user), Title = "Новый тест", IsModified = true };
            return View(q);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Import([Bind(Include = "Id,ClientId,Title,AnswerText,QuizNumber,QuizVersion,IsModified,IsDeleted,Questions")] Quiz quiz)
        {
            return await AddQuiz(quiz);
        }


        // GET: Quizs/Edit/5
        public async Task<ActionResult> Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Quiz quiz = await db.Quizes.Where(q => q.Id == id).Include("Questions.Answers").FirstOrDefaultAsync();

            if (quiz == null)
            {
                return HttpNotFound();
            }
            return View(quiz);
        }

        // POST: Quizs/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Edit([Bind(Include = "Id,ClientId,Title,AnswerText,QuizNumber,QuizVersion,IsModified,IsDeleted,Questions")] Quiz quiz)
        {
            if (ModelState.IsValid)
            {
                quiz.IsModified = true;
                db.Entry(quiz).State = EntityState.Modified;
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }
            return View(quiz);
        }

        // GET: Quizs/Delete/5
        public async Task<ActionResult> Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Quiz quiz = await db.Quizes.FindAsync(id);
            if (quiz == null)
            {
                return HttpNotFound();
            }
            return View(quiz);
        }

        [HttpGet]
        public async Task<ActionResult> DeleteQuestion(int? id)
        {
            if (id==null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }

            Question question = await db.Questions.FindAsync(id);
            if (question==null)
            {
                return HttpNotFound();
            }
            return View(question);
        }

        [HttpPost, ActionName("DeleteQuestion")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> DeleteConfirmedQuestion(int id)
        {
            Question question = await db.Questions.Where(x => x.Id == id).Include("Answers").FirstOrDefaultAsync();
            db.Questions.Remove(question);
            await db.SaveChangesAsync();
            return RedirectToAction("Index");
        }

        // POST: Quizs/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> DeleteConfirmed(int id)
        {
            Quiz quiz = await db.Quizes.Where(q=>q.Id==id).Include("Questions").FirstOrDefaultAsync();

            //foreach(var answer in quiz.Questions)
            //{
            //    db.Entry(answer).State = EntityState.Deleted;
            //}

            //db.Quizes.Remove(quiz);

            quiz.IsDeleted = true;
            db.Entry(quiz).State = EntityState.Modified;

            await db.SaveChangesAsync();
            return RedirectToAction("Index");
        }

        [HttpGet]
        public async Task<ActionResult> AddQuestion(int? quizId)
        {
            if (quizId == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }

            ViewBag.Quiz = await db.Quizes.FindAsync(quizId);

            return View(new Question() { Answers = { new QuestionAnswer(), new QuestionAnswer(), new QuestionAnswer(), new QuestionAnswer(), new QuestionAnswer() }, Difficulty=1 });

        }

        [HttpGet]
        public async Task<ActionResult> EditQuestion(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            var quiestion = await db.Questions.Where(q=>q.Id==id).Include("Answers").FirstOrDefaultAsync();

            return View(quiestion);
        }

        [HttpGet]
        public async Task<ActionResult> Random(int? id)
        {
            if(id==null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            var user = await db.Users.Where(u => u.UserName == User.Identity.Name).FirstOrDefaultAsync();

            var currentQuiz = await db.Quizes.Where(q => q.Id == id).Include("Questions.Answers").FirstOrDefaultAsync();

            if (currentQuiz.ParentQuizId!=0)
            {
                int pId = currentQuiz.ParentQuizId;

                currentQuiz = await db.Quizes.Where(q => q.Id == pId).Include("Questions.Answers").FirstOrDefaultAsync();
            }

            Quiz newQuiz = Quiz.GetRandomizeQuiz(currentQuiz, NumberGenerator.GetQuizNumber(user));

            newQuiz.AnswerText = Quiz.SerializeAnswers(newQuiz.Questions.ToList());


            db.Entry(newQuiz).State = EntityState.Added;

            foreach (var question in newQuiz.Questions)
            {
                db.Entry(question).State = EntityState.Added;
               // db.Questions.Add(question);

                foreach (var answer in question.Answers)
                {
                    db.Entry(answer).State = EntityState.Added;
                    //db.QuestionAnswers.Add(answer);
                }
            }

           // db.Quizes.Add(newQuiz);


            user.Quizs.Add(newQuiz);
            db.Entry(user).State = EntityState.Modified;


            await db.SaveChangesAsync();

            return RedirectToAction("Index");
        }

        [HttpGet]
        public async Task<FileContentResult> GetPdfFile(int? id)
        {
            byte[] contents = await GeneratePdfFile(id);
            Response.AddHeader("Content-Disposition", $"inline; filename=test{id}.pdf");


            return File(contents, "application/pdf");

        }

        private async Task<byte[]>  GeneratePdfFile(int? id)
        {
            //var user = await db.Users.Where(u => u.UserName == User.Identity.Name).FirstOrDefaultAsync();

            var quiz = await db.Quizes.Where(q => q.Id == id).Include("Questions.Answers").FirstOrDefaultAsync();

            var parentQuiz = await db.Quizes.Where(q => q.Id == quiz.ParentQuizId).FirstOrDefaultAsync();

            

            string ttf = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Fonts), "ARIAL.TTF");
            var baseFont = BaseFont.CreateFont(ttf, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            var font = new iTextSharp.text.Font(baseFont, iTextSharp.text.Font.DEFAULTSIZE, iTextSharp.text.Font.NORMAL);
            var fontBold = new iTextSharp.text.Font(baseFont, iTextSharp.text.Font.DEFAULTSIZE, iTextSharp.text.Font.BOLD);
            var fontHead = new iTextSharp.text.Font(baseFont, 10.0f, iTextSharp.text.Font.NORMAL);

            using (MemoryStream myMemoryStream = new MemoryStream())
            {
                Document myDocument = new Document();
                PdfWriter myPDFWriter = PdfWriter.GetInstance(myDocument, myMemoryStream);

                myDocument.Open();

                PdfPTable table = new PdfPTable(1);
                table.WidthPercentage = 100;

                table.DefaultCell.Border = Rectangle.NO_BORDER;

                string quizTitle = string.Empty;
                string quizCode = quiz.QuizNumber.ToString();

                if (parentQuiz!=null)
                {
                    quizTitle += parentQuiz.Title;
                }
                else
                {
                    quizTitle += quiz.Title;
                }

                myDocument.Add(new Paragraph($"Тест: {quizTitle}", fontBold));
                myDocument.Add(new Paragraph($"Код теста: {quizCode}", fontBold));
                myDocument.Add(new Paragraph(" "));
                myDocument.Add(new Paragraph("Для прохождения теста необходимо отметить на бланке ответов 'Код теста'.", fontHead));
                myDocument.Add(new Paragraph("При ответе необходимо поставить метку в соответствуещем поле, метка не должна выходить за пределы метки-квадрата.", fontHead));
                myDocument.Add(new Paragraph("Правильных ответов на вопрос может быть один или несколько.", fontHead));
                myDocument.Add(new Paragraph(" "));
                myDocument.Add(new Paragraph(" "));

                int i = 1;

                foreach(var question in quiz.Questions)
                {
                    PdfPCell line = new PdfPCell();

                    var p1 = new Paragraph($"Вопрос №{i++}", fontBold);

                    line.AddElement(p1);

                    line.AddElement(new Paragraph($"{question.QuestionText}", font));
                    line.AddElement(new Paragraph($"A - {question.Answers[0].Answer}", fontHead));
                    line.AddElement(new Paragraph($"B - {question.Answers[1].Answer}", fontHead));
                    line.AddElement(new Paragraph($"C - {question.Answers[2].Answer}", fontHead));
                    line.AddElement(new Paragraph($"D - {question.Answers[3].Answer}", fontHead));
                    line.AddElement(new Paragraph($"E - {question.Answers[4].Answer}", fontHead));
                    line.Border = Rectangle.NO_BORDER;

                    table.AddCell(line);
                }

                myDocument.Add(table);

                myDocument.Close();

                byte[] content = myMemoryStream.ToArray();

                return content;

            }
        }

        [HttpPost]
        public async Task<ActionResult> EditQuestion([Bind(Include = "Id,Difficulty,QuestionText,Answers")] Question question)
        {
            if (ModelState.IsValid)
            {
                 db.Entry(question).State = EntityState.Modified;

                foreach (var item in question.Answers)
                {
                    db.Entry(item).State = EntityState.Modified;

                    await db.SaveChangesAsync();
                }
            }
            //return View(question);
            return RedirectToAction("Index");
        }

        [HttpPost]
        public async Task<ActionResult> AddQuestion([Bind(Include = "Id,Difficulty,QuestionText,Answers")] Question question)
        {
            if (ModelState.IsValid)
            {
                foreach (var item in question.Answers)
                {
                        db.Entry(item).State = EntityState.Added;
                }

                Quiz q = await db.Quizes.FindAsync(int.Parse(Request.Params["quizId"]));

                q.Questions.Add(question);

                q.AnswerText = Quiz.SerializeAnswers(q.Questions.ToList());

                db.Entry(q).State = EntityState.Modified;

                db.Entry(question).State = EntityState.Added;
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }
            return View(question);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }


        [HttpPost]
        public async Task<ActionResult> Upload(HttpPostedFileBase upload, string title)
        {
            if (upload != null)
            {
                string fileName = Path.GetFileName(upload.FileName);
                byte[] binData = new byte[upload.ContentLength];
                upload.InputStream.Read(binData, 0, upload.ContentLength);

                Stream stream = new MemoryStream(binData);

                string result = System.Text.Encoding.Default.GetString(binData);

                var user = await db.Users.Where(u => u.UserName == User.Identity.Name).FirstOrDefaultAsync();

                Quiz quiz = new Quiz() { Title = title, QuizNumber = NumberGenerator.GetQuizNumber(user), IsModified = true };

                using (ExcelPackage package = new ExcelPackage(stream))
                {
                    ExcelWorksheet worksheet = package.Workbook.Worksheets[1];

                    for (int i = 1; i <= worksheet.Dimension.Rows; i++)
                    {
                        try
                        {
                            Question question = new Question() { QuestionText = worksheet.Cells[i, 1].Value.ToString(), Difficulty = int.Parse(worksheet.Cells[i, 2].Value.ToString()) };


                            question.Answers.Add(ConvertToQuestionAnswer(worksheet.Cells[i, 3].Value?.ToString()));
                            question.Answers.Add(ConvertToQuestionAnswer(worksheet.Cells[i, 4].Value?.ToString()));
                            question.Answers.Add(ConvertToQuestionAnswer(worksheet.Cells[i, 5].Value?.ToString()));
                            question.Answers.Add(ConvertToQuestionAnswer(worksheet.Cells[i, 6].Value?.ToString()));
                            question.Answers.Add(ConvertToQuestionAnswer(worksheet.Cells[i, 7].Value?.ToString()));

                            quiz.Questions.Add(question);

                            db.Entry(question).State = EntityState.Added;
                        }
                        catch
                        {
                            Debug.Write("Skip question №"+i);
                        }
                    }
                }

                quiz.AnswerText = Quiz.SerializeAnswers(quiz.Questions.ToList());

                db.Entry(quiz).State = EntityState.Added;

                user.Quizs.Add(quiz);

                db.Entry(user).State = EntityState.Modified;

                await db.SaveChangesAsync();
            }


            return RedirectToAction("Index");
        }

        private QuestionAnswer ConvertToQuestionAnswer(string txt)
        {
            if (string.IsNullOrEmpty(txt))
            {
                return new QuestionAnswer() { Answer = string.Empty, RightAnswer = false};
            }
            bool isRight = false;
            if (!string.IsNullOrEmpty(txt))
            {
                if (txt[0] == '*')
                {
                    isRight = true;
                    txt = txt.Remove(0, 1);
                }
            }

            return new QuestionAnswer() { Answer = txt, RightAnswer = isRight };
        }
    }
}
