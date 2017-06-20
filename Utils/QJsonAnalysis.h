#ifndef QJSONANALYSIS_H
#define QJSONANALYSIS_H
#include <QByteArray>
#include <QJsonObject>
#include <QJsonArray>
#include <QJsonValue>
#include <QJsonDocument>
#include <QJsonParseError>

// JsonPrivate类前向声明
struct JsonPrivate
{
    JsonPrivate(const QString &jsonOrJsonFilePath, bool fromFile);
    JsonPrivate(const QByteArray &jsonByteArray);
    void setValue(QJsonObject &parent, const QString &path, const QJsonValue &newValue);
    QJsonValue getValue(const QString &path, const QJsonObject &fromNode) const;
    QJsonObject root;   // Json的根节点
};

/**
 * Qt 的 Json API 读写多层次的属性不够方便，这个类的目的就是能够使用带 "." 的路径格式访问 Json 的属性，例如
 * "id" 访问的是根节点下的 id，"user.address.street" 访问根节点下 user 的 address 的 street 的属性。
 *
 * Json 例子：
 * {
 *     "id": 18191,
 *     "user": {
 *         "address": {
 *             "street": "Wiessenstrasse",
 *             "postCode": "100001"
 *         },
 *         "childrenNames": ["Alice", "Bob", "John"]
 *     }
 * }
 *
 * 访问 id:     Json.getInt("id")，返回 18191
 * 访问 street: Json.getString("user.address.street")，返回 "Wiessenstrasse"
 * 访问 childrenNames: Json.getStringList("user.childrenNames") 得到字符串列表("Alice", "Bob", "John")
 * 设置 "user.address.postCode" 则可以使用 Json.set("user.address.postCode", "056231")
 *
 * 如果读取的属性不存在，则返回指定的默认值，如 "database.username.firstName" 不存在，
 * 调用 Json.getString("database.username.firstName", "defaultName")，由于要访问的属性不存在，
 * 得到的是一个空的 QJsonValue，所以返回我们指定的默认值 "defaultName"。
 *
 * 如果要修改的属性不存在，则会自动的先创建属性，然后设置它的值。
 *
 * 注意: Json 文件要使用 UTF-8 编码。
 */
class QJsonAnalysis
{
public:
    /**
     * 使用 Json字符串或者从文件读取Json内容创建Json对象。
     * 使用 sonByteArray创建Json对象。
     * 如果 fromFile为true，则jsonOrJsonFilePath为文件的路径
     * 如果 fromFile为false，则jsonOrJsonFilePath为Json的字符串内容
     *
     * @param jsonOrJsonFilePath Json的字符串内容或者Json文件的路径
     * @param fromFile为true，则jsonOrJsonFilePath为文件的路径；为false，则jsonOrJsonFilePath为Json的字符串内容
     */
    explicit QJsonAnalysis(const QString &jsonOrJsonFilePath = "{}", bool fromFile = false);
    explicit QJsonAnalysis(const QByteArray &jsonByteArray);
    ~QJsonAnalysis();

    /**
     * 读取路径 path 对应属性的整数值
     *
     * @param path带"."的路径格
     * @param def如果要找的属性不存在时返回的默认值
     * @param fromNode从此节点开始查找，如果为默认值，则从Json的根节点开始查找
     * @return 整数值
     */
    int         getInt(const QString &path, int def = 0, const QJsonObject &fromNode = QJsonObject()) const;
    bool        getBool(const QString &path, bool def = false, const QJsonObject &fromNode = QJsonObject()) const;
    double      getDouble(const QString &path, double def = 0.0, const QJsonObject &fromNode = QJsonObject()) const;
    QString     getString(const QString &path, const QString &def = QString(), const QJsonObject &fromNode = QJsonObject()) const;
    QStringList getStringList(const QString &path, const QJsonObject &fromNode = QJsonObject()) const;
    QJsonArray  getJsonArray(const QString &path, const QJsonObject &fromNode = QJsonObject()) const;
    QJsonValue  getJsonValue(const QString &path, const QJsonObject &fromNode = QJsonObject()) const;
    QJsonObject getJsonObject(const QString &path, const QJsonObject &fromNode = QJsonObject()) const;

    /**
     * 读取路径 path 对应属性的整数值
     *
     * @return Json文件原始QByteArray
     *
     */
    QByteArray getJsonRawByte();

    /**
     * @brief 设置path对应的Json属性的值
     * @param path path带"."的路径格
     * @param value可以是整数/浮点数/字符串/QJsonValue/QJsonObject等，具体请参考QJsonValue的构造函数
     */
    void set(const QString &path, const QJsonValue &value);
    void set(const QString &path, const QStringList &strings);

    /**
     * @brief 把Json保存到文件
     * @param path文件的路径
     */
    void save(const QString &path, QJsonDocument::JsonFormat format = QJsonDocument::Indented);
    QString toString(QJsonDocument::JsonFormat format = QJsonDocument::Indented) const;

private:
    JsonPrivate *jsondata;
};

#endif
