using System;
using System.Data.Entity;
using BL;

namespace Quizzy.Models
{
    public class QuizzyContext : DbContext
    {
        public QuizzyContext():base ("DefaultConnection")
        {

        }

        public DbSet<Respondent> Respondents { get; set; }

        public DbSet<Quiz> Quizes { get; set; }

        public DbSet<Answer> Answers { get; set; }

        public DbSet<User> Users { get; set; }

        public DbSet<Group> Groups { get; set; }

        public DbSet<Question> Questions { get; set; }

        public DbSet<QuestionAnswer> QuestionAnswers { get; set; }


        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>().
                Property(p => p.LastSyncTime)
                .HasColumnType("datetime2")
                .HasPrecision(0)
                .IsRequired();

            modelBuilder.Entity<Group>().HasMany(c => c.Quizs)
                .WithMany(s => s.Groups)
                .Map(t => t.MapLeftKey("GroupId")
                .MapRightKey("QuizId")
                .ToTable("GroupQuiz"));
        }

        //modelBuilder.Entity<Question>()
        //    .HasOptional(a => a.Answers)
        //    .WithOptionalDependent()
        //    .WillCascadeOnDelete(true);
    }
}
