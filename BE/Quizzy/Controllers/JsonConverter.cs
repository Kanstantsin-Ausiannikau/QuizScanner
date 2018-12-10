using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;
using Quizzy.Models;

namespace Quizzy.Controllers
{
    internal class JsonConverter
    {
        internal static string GetGroupsJsonData(ICollection<Group> groups)
        {
            string json = JsonConvert.SerializeObject(groups);

            return json;
        }
    }
}