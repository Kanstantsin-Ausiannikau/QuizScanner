using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(Quizzy.Startup))]
namespace Quizzy
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);

        }
    }
}
