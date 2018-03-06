#include "InfoSizeForm.h"
#include "ui_InfoSizeForm.h"

InfoSizeForm::InfoSizeForm(QWidget *parent) : QWidget(parent), ui(new Ui::InfoSizeWidget)
{
    ui->setupUi(this);
    this->move((parent->width() - this->width()) / 2, (parent->width() - this->width()) / 2 - 150);
}

InfoSizeForm::~InfoSizeForm()
{
    delete ui;
}

void InfoSizeForm::on_pushButton_clicked()
{
    this->setVisible(false);
}

// widget拖动
void InfoSizeForm::mousePressEvent(QMouseEvent *event)
{
    ismoveing = true;
    moveposition = event->globalPos() - pos();
    return QWidget::mousePressEvent(event);
}
void InfoSizeForm::mouseMoveEvent(QMouseEvent *event)
{
    if(ismoveing && (event->buttons() && Qt::LeftButton) &&
      (event->globalPos()-moveposition).manhattanLength() > QApplication::startDragDistance())
    {
        move(event->globalPos()-moveposition);
        moveposition = event->globalPos() - pos();
    }
    return QWidget::mouseMoveEvent(event);
}

void InfoSizeForm::mouseReleaseEvent(QMouseEvent *event)
{
    ismoveing = false;
}

void InfoSizeForm::Show(QJsonAnalysis* p_buttonjson)
{
    QString jsonparentstr("taskSize");
    if(!p_buttonjson->getJsonObject(jsonparentstr).isEmpty())
    {
        double temp = 0.0;
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "outDia");
        ui->Awj->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "outDiaDevUp");
        ui->Awjspc->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "outDiaDevDown");
        ui->Awjxpc->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "holeDia");
        ui->Akj->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "holeDiaDevUp");
        ui->Akjspc->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "holeDiaDevDown");
        ui->Akjxpc->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "holeDist");
        ui->Akju->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "holeDistDevUp");
        ui->Akjuspc->setText(QString("%1").arg(temp));
        temp = p_buttonjson->getDouble(jsonparentstr + "." + "holeDistDevDown");
        ui->Akjuxpc->setText(QString("%1").arg(temp));
    }
}

void InfoSizeForm::Update(qint32 buf[])
{
    ui->Bwj->setText(QString::number(buf[0] / 100.0));
    ui->Bwjpc->setText(QString::number(buf[1] / 100.0));
    ui->Bkj1->setText(QString::number(buf[2] / 100.0));
    ui->Bkj2->setText(QString::number(buf[3] / 100.0));
    ui->Bkj3->setText(QString::number(buf[4] / 100.0));
    ui->Bkj4->setText(QString::number(buf[5] / 100.0));
    ui->Bkju1->setText(QString::number(buf[6] / 100.0));
    ui->Bkju2->setText(QString::number(buf[7] / 100.0));
    ui->Bkju3->setText(QString::number(buf[8] / 100.0));
    ui->Bkju4->setText(QString::number(buf[9] / 100.0));
    ui->Bkpy->setText(QString::number(buf[10] / 100.0));
    //ui->Bbpjg->setText(QString::number(buf[11] / 100.0));
}
