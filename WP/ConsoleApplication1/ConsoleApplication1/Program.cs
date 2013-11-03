using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            var readLine = Console.ReadLine();
            while (readLine != "e")
            {
                readLine = Console.ReadLine();
                var i = Int32.Parse(readLine);
                Console.WriteLine((char)i);
            }
        }
    }
}
