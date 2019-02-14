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
    public class RespondentsController : Controller
    {
        private QuizzyContext db = new QuizzyContext();

        // GET: Respondents
        public async Task<ActionResult> Index()
        {
            return View(await db.Respondents.ToListAsync());
        }

        // GET: Respondents/Details/5
        public async Task<ActionResult> Details(long? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Respondent respondent = await db.Respondents.FindAsync(id);
            if (respondent == null)
            {
                return HttpNotFound();
            }
            return View(respondent);
        }

        // GET: Respondents/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: Respondents/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Create([Bind(Include = "Id,RespondentNumber,FirstName,LastName,IsModified,IsDeleted")] Respondent respondent)
        {
            if (ModelState.IsValid)
            {
                respondent.IsModified = true;
                db.Respondents.Add(respondent);
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }

            return View(respondent);
        }

        // GET: Respondents/Edit/5
        public async Task<ActionResult> Edit(long? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Respondent respondent = await db.Respondents.FindAsync(id);
            if (respondent == null)
            {
                return HttpNotFound();
            }
            return View(respondent);
        }

        // POST: Respondents/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Edit([Bind(Include = "Id,RespondentNumber,FirstName,LastName,IsModified,IsDeleted")] Respondent respondent)
        {
            if (ModelState.IsValid)
            {

                respondent.IsModified = true;
                db.Entry(respondent).State = EntityState.Modified;
                await db.SaveChangesAsync();
                return RedirectToAction("Index", "Groups");
            }
            return View(respondent);
        }

        // GET: Respondents/Delete/5
        public async Task<ActionResult> Delete(long? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Respondent respondent = await db.Respondents.FindAsync(id);
            if (respondent == null)
            {
                return HttpNotFound();
            }
            return View(respondent);
        }

        // POST: Respondents/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> DeleteConfirmed(long id)
        {
            Respondent respondent = await db.Respondents.Where(r => r.Id == id).Include("Answers").FirstOrDefaultAsync();

            foreach(var a in respondent.Answers.ToList())
            {
                db.Entry(a).State = EntityState.Deleted;
            }

            respondent.IsDeleted = true;
            db.Respondents.Remove(respondent);
            await db.SaveChangesAsync();
            return RedirectToAction("Index", "Groups");
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
