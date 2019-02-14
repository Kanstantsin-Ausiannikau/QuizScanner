using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Quizzy.Models;
using BL;
using Quizzy.DAL;

namespace Quizzy.Controllers
{
    [Authorize]
    public class GroupsController : Controller
    {
        private QuizzyContext db = new QuizzyContext();

        // GET: Groups
        public async Task<ActionResult> Index()
        {
            var user = await db.Users.Where(u => u.UserName == User.Identity.Name).Include("Groups.Respondents").FirstOrDefaultAsync();

            return View(user.Groups.ToList());
        }

        // GET: Groups/Details/5
        public async Task<ActionResult> Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            //Group group = await db.Groups.FindAsync(id);

            Group group = await db.Groups.Where(g => g.Id == id).Include("Quizs").FirstOrDefaultAsync();

            if (group == null)
            {
                return HttpNotFound();
            }
            return View(group);
        }

        // GET: Groups/Create
        public ActionResult Create()
        {
            return View();
        }

        // GET: Groups/ViewRespondents/1
        public async Task<ActionResult> ViewRespondents(int? id)
        {
            var group = await db.Groups.Where(g => g.Id == id).Include("Respondents").FirstOrDefaultAsync();

            return View(group);
        }

        // POST: Groups/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Create([Bind(Include = "Id,GroupName,IsModified,IsDeleted")] Group group)
        {
            if (ModelState.IsValid)
            {
                User user = await db.Users.Where(u=>u.UserName==User.Identity.Name).FirstOrDefaultAsync();

                group.IsModified = true;

                user.Groups.Add(group);
                db.Entry(user).State = EntityState.Modified;

                db.Groups.Add(group);
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }

            return View(group);
        }

        // GET: Groups/Edit/5
        public async Task<ActionResult> Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Group group = await db.Groups.FindAsync(id);
            if (group == null)
            {
                return HttpNotFound();
            }
            return View(group);
        }

        // POST: Groups/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Edit([Bind(Include = "Id,GroupName,IsModified,IsDeleted")] Group group)
        {
            if (ModelState.IsValid)
            {
                group.IsModified = true;

                db.Entry(group).State = EntityState.Modified;
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }
            return View(group);
        }

        // GET: Groups/Delete/5
        public async Task<ActionResult> Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Group group = await db.Groups.FindAsync(id);
            if (group == null)
            {
                return HttpNotFound();
            }
            return View(group);
        }


        //GET: Groups/CreateRespondent/1
        public ActionResult CreateRespondent(int id)
        {
            ViewBag.GroupId = id;
            var respondent = new Respondent() {
                RespondentNumber = NumberGenerator.GetRespondentNumber(db.Users.Where(u=>u.UserName==User.Identity.Name).FirstOrDefault()),
                IsModified = true };
            return View(respondent);
        }

        [HttpPost]
        public async Task<ActionResult> CreateRespondent([Bind(Include = "Id,RespondentNumber,FirstName,LastName,IsModified,IsDeleted")] Respondent respondent, HiddenInputAttribute hidden)
        {
            Group group;
            if (ModelState.IsValid)
            {
                int groupId = int.Parse(Request.Params["GroupId"]);

                group = await db.Groups.Where(g => g.Id == groupId).FirstOrDefaultAsync();

                respondent.IsModified = true;
                group.Respondents.Add(respondent);

                db.Respondents.Add(respondent);
                await db.SaveChangesAsync();
                return RedirectToAction("ViewRespondents", new { id = groupId });
            }
            return View();
        }

        [HttpGet]
        public async Task<ActionResult> AttachQuiz(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }

            var user = await db.Users.Where(u => u.UserName == User.Identity.Name).Include("Groups").FirstOrDefaultAsync();

            Group group = user.Groups.Where(g => g.Id == id).FirstOrDefault();

            if (group == null)
            {
                return HttpNotFound();
            }

            var q = (await db.Users.Where(u => u.UserName == User.Identity.Name).Include("Quizs").FirstOrDefaultAsync()).Quizs;

            group.Quizs.Add(new Quiz());

            SelectList quizs = new SelectList(q, "Id", "Title");

            ViewBag.Quizs = quizs;

            return View(group);
        }

        [HttpPost]
        public async Task<ActionResult> AttachQuiz(Group group, string ddlist)
        {
            if (ModelState.IsValid)
            {
                var quiz = await db.Quizes.FindAsync(int.Parse(ddlist));

                group.Quizs.Add(quiz);
                quiz.Groups.Add(group);

                db.Entry(quiz).State = EntityState.Modified;
                db.Entry(group).State = EntityState.Modified;

                await db.SaveChangesAsync();

                return RedirectToAction("Index");
            }
            return View(group);
        }

        // POST: Groups/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> DeleteConfirmed(int id)
        {
            Group group = await db.Groups.FindAsync(id);
            group.IsDeleted = true;

            db.Groups.Remove(group);
            await db.SaveChangesAsync();
            return RedirectToAction("Index");
        }

        [HttpPost]
        public async Task<ActionResult> Upload(HttpPostedFileBase upload, int? groupId)
        {
            if (upload != null)
            {
                // получаем имя файла
                string fileName = System.IO.Path.GetFileName(upload.FileName);
                // сохраняем файл в папку Files в проекте
                byte[] binData = new byte[upload.ContentLength];
                upload.InputStream.Read(binData, 0, upload.ContentLength);

                string result = System.Text.Encoding.Default.GetString(binData);

                string[] txt = result.Split(new char[] { '\n', '\t', '\r' }, StringSplitOptions.RemoveEmptyEntries);

                Group group;

                if (txt.Length > 0)
                {

                    if (groupId != null)
                    {
                        group = await db.Groups.FindAsync(groupId);
                    }
                    else
                    {
                        throw new Exception();
                    }

                    foreach (var item in txt)
                    {
                        string[] fio = item.Split(new char[] { ';' });

                        if (fio.Length==2)
                        {
                            Respondent r = new Respondent() {
                                LastName = fio[0],
                                FirstName = fio[1],
                                RespondentNumber = NumberGenerator.GetRespondentNumber(db.Users.Where(u => u.UserName == User.Identity.Name).FirstOrDefault()),
                                IsModified = true
                            };

                            group.Respondents.Add(r);
                            db.Entry(r).State = EntityState.Added;
                        }
                        else
                        {
                            throw new Exception();
                        }

                        await db.SaveChangesAsync();

                    }
                }
                else
                {
                    throw new Exception();
                }

                //upload.SaveAs(Server.MapPath("~/Files/" + fileName));
            }
            return RedirectToAction("Index");
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
