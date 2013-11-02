using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;

namespace BatMobile
{
    public partial class MainPage
    {
        // Constructor
        public MainPage()
        {
            InitializeComponent();
            Touch.FrameReported += Touch_FrameReported;
        }

        private const int mainPoint = 0;
        double preXArray;
        double preYArray;

        void Touch_FrameReported(object sender, TouchFrameEventArgs e)
        {
            TouchPointCollection pointCollection = e.GetTouchPoints(drawCanvas);

            if (pointCollection[mainPoint].Action == TouchAction.Down)
            {
                drawCanvas.Children.Clear();
                preXArray = pointCollection[mainPoint].Position.X;
                preYArray = pointCollection[mainPoint].Position.Y;
            }
            if (pointCollection[mainPoint].Action == TouchAction.Move)
            {
                var line = new Line
                {
                    X1 = preXArray,
                    Y1 = preYArray,
                    X2 = pointCollection[mainPoint].Position.X,
                    Y2 = pointCollection[mainPoint].Position.Y,
                    Stroke = new SolidColorBrush(Colors.Black),
                    Fill = new SolidColorBrush(Colors.Black)
                };

                drawCanvas.Children.Add(line);

                preXArray = pointCollection[mainPoint].Position.X;
                preYArray = pointCollection[mainPoint].Position.Y;
            }
        }
    }
}